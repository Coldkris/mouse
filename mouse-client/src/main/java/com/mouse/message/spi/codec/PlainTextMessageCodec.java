/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.spi.codec;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

import com.mouse.message.Event;
import com.mouse.message.Heartbeat;
import com.mouse.message.Message;
import com.mouse.message.Metric;
import com.mouse.message.Trace;
import com.mouse.message.Transaction;
import com.mouse.message.internal.DefaultEvent;
import com.mouse.message.internal.DefaultHeartbeat;
import com.mouse.message.internal.DefaultMessageTree;
import com.mouse.message.internal.DefaultMetric;
import com.mouse.message.internal.DefaultTrace;
import com.mouse.message.internal.DefaultTransaction;
import com.mouse.message.spi.MessageCodec;
import com.mouse.message.spi.MessageTree;

import io.netty.buffer.ByteBuf;

/**
 * 纯文本消息编解码器
 * @author kris
 * @version $Id: PlainTextMessageCodec.java, v 0.1 2018年6月4日 上午9:07:17 kris Exp $
 */
public class PlainTextMessageCodec implements MessageCodec, LogEnabled {

    public static final String   ID           = "plain-text";

    private static final String  VERSION      = "PT1";                              // 版本1

    private static final byte    TAB          = '\t';                               // 制表符

    private static final byte    LF           = '\n';                               // 换行符

    private BufferWriter         bWriter      = new EscapingBufferWriter();

    private BufferHelper         bufferHelper = new BufferHelper(bWriter);

    private DateHelper           dateHelper   = new DateHelper();

    private ThreadLocal<Context> ctx          = new ThreadLocal<Context>() {
                                                  @Override
                                                  protected Context initialValue() {
                                                      return new Context();
                                                  }
                                              };

    private Logger               logger;

    @Override
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    @Override
    public MessageTree decode(ByteBuf buf) {
        MessageTree tree = new DefaultMessageTree();

        decode(buf, tree);
        return tree;
    }

    @Override
    public void decode(ByteBuf buf, MessageTree tree) {
        Context context = ctx.get().setBuffer(buf);

        decodeHeader(context, tree);

        if (buf.readableBytes() > 0) {
            decodeMessage(context, tree);
        }
    }

    protected void decodeHeader(Context ctx, MessageTree tree) {
        BufferHelper helper = bufferHelper;
        String id = helper.read(ctx, TAB);
        String domain = helper.read(ctx, TAB);
        String hostName = helper.read(ctx, TAB);
        String ipAddress = helper.read(ctx, TAB);
        String threadGroupName = helper.read(ctx, TAB);
        String threadId = helper.read(ctx, TAB);
        String threadName = helper.read(ctx, TAB);
        String messageId = helper.read(ctx, TAB);
        String parentMessageId = helper.read(ctx, TAB);
        String rootMessageId = helper.read(ctx, TAB);
        String sessionToken = helper.read(ctx, LF);

        if (VERSION.equals(id)) {
            tree.setDomain(domain);
            tree.setHostName(hostName);
            tree.setIpAddress(ipAddress);
            tree.setThreadGroupName(threadGroupName);
            tree.setThreadId(threadId);
            tree.setThreadName(threadName);
            tree.setMessageId(messageId);
            tree.setParentMessageId(parentMessageId);
            tree.setRootMessageId(rootMessageId);
            tree.setSessionToken(sessionToken);
        } else {
            throw new RuntimeException(String.format("无法识别的纯文本消息编解码器id(%s)!", id));
        }

    }

    private void decodeMessage(Context ctx, MessageTree tree) {
        Stack<DefaultTransaction> stack = new Stack<>();
        Message parent = decodeLine(ctx, null, stack);

        tree.setMessage(parent);

        while (ctx.getBuffer().readableBytes() > 0) {
            Message message = decodeLine(ctx, (DefaultTransaction) parent, stack);

            if (message instanceof DefaultTransaction) {
                parent = message;
            } else {
                break;
            }
        }
    }

    private Message decodeLine(Context ctx, DefaultTransaction parent, Stack<DefaultTransaction> stack) {
        BufferHelper helper = bufferHelper;
        byte identifier = ctx.getBuffer().readByte();
        String timestamp = helper.read(ctx, TAB);
        String type = helper.read(ctx, TAB);
        String name = helper.read(ctx, TAB);

        switch (identifier) {
            case 't':
                DefaultTransaction transaction = new DefaultTransaction(type, name, null);

                helper.read(ctx, LF); //去除换行
                transaction.setTimestamp(dateHelper.parse(timestamp));

                if (parent != null) {
                    parent.addChild(transaction);
                }

                stack.push(parent);
                return transaction;
            case 'A':
                DefaultTransaction tran = new DefaultTransaction(type, name, null);
                String status = helper.read(ctx, TAB);
                String duration = helper.read(ctx, TAB);
                String data = helper.read(ctx, TAB);

                helper.read(ctx, LF); //去除换行
                tran.setTimestamp(dateHelper.parse(timestamp));
                tran.setStatus(status);
                tran.addData(data);

                long d = Long.parseLong(duration.substring(0, duration.length() - 2));
                tran.setDurationInMicros(d);

                if (parent != null) {
                    parent.addChild(tran);
                    return parent;
                } else {
                    return tran;
                }
            case 'T':
                String transactionStatus = helper.read(ctx, TAB);
                String transactionDuration = helper.read(ctx, TAB);
                String transactionData = helper.read(ctx, TAB);

                helper.read(ctx, LF); //去除换行
                parent.setStatus(transactionStatus);
                parent.addData(transactionData);

                long transactionD = Long.parseLong(transactionDuration.substring(0, transactionDuration.length() - 2));

                parent.setDurationInMicros(transactionD);

                return stack.pop();
            case 'E':
                DefaultEvent event = new DefaultEvent(type, name);
                String eventStatus = helper.read(ctx, TAB);
                String eventData = helper.read(ctx, TAB);

                helper.read(ctx, LF); //去除换行
                event.setTimestamp(dateHelper.parse(timestamp));
                event.setStatus(eventStatus);
                event.addData(eventData);

                if (parent != null) {
                    parent.addChild(event);
                    return parent;
                } else {
                    return event;
                }
            case 'M':
                DefaultMetric metric = new DefaultMetric(type, name);
                String metricStatus = helper.read(ctx, TAB);
                String metricData = helper.read(ctx, TAB);

                helper.read(ctx, LF); //去除换行
                metric.setTimestamp(dateHelper.parse(timestamp));
                metric.setStatus(metricStatus);
                metric.addData(metricData);

                if (parent != null) {
                    parent.addChild(metric);
                    return parent;
                } else {
                    return metric;
                }
            case 'L':
                DefaultTrace trace = new DefaultTrace(type, name);
                String traceStatus = helper.read(ctx, TAB);
                String traceData = helper.read(ctx, TAB);

                helper.read(ctx, LF); //去除换行
                trace.setTimestamp(dateHelper.parse(timestamp));
                trace.setStatus(traceStatus);
                trace.addData(traceData);

                if (parent != null) {
                    parent.addChild(trace);
                    return parent;
                } else {
                    return trace;
                }
            case 'H':
                DefaultHeartbeat heartbeat = new DefaultHeartbeat(type, name);
                String heartbeatStatus = helper.read(ctx, TAB);
                String heartbeatData = helper.read(ctx, TAB);

                helper.read(ctx, LF); //去除换行
                heartbeat.setTimestamp(dateHelper.parse(timestamp));
                heartbeat.setStatus(heartbeatStatus);
                heartbeat.addData(heartbeatData);

                if (parent != null) {
                    parent.addChild(heartbeat);
                    return parent;
                } else {
                    return heartbeat;
                }
            default:
                logger.warn("未知的消息标识符名称(" + (char) identifier + "): " + ctx.getBuffer().toString(Charset.forName("utf-8")));
                throw new RuntimeException("未知标识符名称(" + identifier + ")!");
        }
    }

    @Override
    public void encode(MessageTree tree, ByteBuf buf) {
        int count = 0;
        int index = buf.writerIndex();

        buf.writeInt(0); // 占位
        count += encodeHeader(tree, buf);

        if (tree.getMessage() != null) {
            count += encodeMessage(tree.getMessage(), buf);
        }

        buf.setInt(index, count);
    }

    protected int encodeHeader(MessageTree tree, ByteBuf buf) {
        BufferHelper helper = bufferHelper;
        int count = 0;

        count += helper.write(buf, VERSION);
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getDomain());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getHostName());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getIpAddress());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getThreadGroupName());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getThreadId());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getThreadName());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getMessageId());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getParentMessageId());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getRootMessageId());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getSessionToken());
        count += helper.write(buf, LF);

        return count;
    }

    public int encodeMessage(Message message, ByteBuf buf) {
        if (message instanceof Transaction) {
            Transaction transaction = (Transaction) message;
            List<Message> children = transaction.getChildren();

            if (children.isEmpty()) {
                return encodeLine(transaction, buf, 'A', Policy.WITH_DURATION);
            } else {
                int count = 0;
                int len = children.size();

                count += encodeLine(transaction, buf, 't', Policy.WITHOUT_STATUS);

                for (int i = 0; i < len; i++) {
                    Message child = children.get(i);

                    if (child != null) {
                        count += encodeMessage(child, buf);
                    }
                }

                count += encodeLine(transaction, buf, 'T', Policy.WITH_DURATION);

                return count;
            }
        } else if (message instanceof Event) {
            return encodeLine(message, buf, 'E', Policy.DEFAULT);
        } else if (message instanceof Trace) {
            return encodeLine(message, buf, 'L', Policy.DEFAULT);
        } else if (message instanceof Metric) {
            return encodeLine(message, buf, 'M', Policy.DEFAULT);
        } else if (message instanceof Heartbeat) {
            return encodeLine(message, buf, 'H', Policy.DEFAULT);
        } else {
            throw new RuntimeException(String.format("不支持的消息类型：%s", message));
        }
    }

    private int encodeLine(Message message, ByteBuf buf, char type, Policy policy) {
        BufferHelper helper = bufferHelper;
        int count = 0;

        count += helper.write(buf, (byte) type);

        if (type == 'T' && message instanceof Transaction) {
            long duration = ((Transaction) message).getDurationInMillis();

            count += helper.write(buf, dateHelper.format(message.getTimestamp() + duration));
        } else {
            count += helper.write(buf, dateHelper.format(message.getTimestamp()));
        }

        count += helper.write(buf, TAB);
        count += helper.writeRaw(buf, message.getType());
        count += helper.write(buf, TAB);
        count += helper.writeRaw(buf, message.getName());
        count += helper.write(buf, TAB);

        if (policy != Policy.WITHOUT_STATUS) {
            count += helper.writeRaw(buf, message.getStatus());
            count += helper.write(buf, TAB);

            Object data = message.getData();

            if (policy == Policy.WITH_DURATION && message instanceof Transaction) {
                long duration = ((Transaction) message).getDurationInMicros();

                count += helper.write(buf, String.valueOf(duration));
                count += helper.write(buf, "us");
                count += helper.write(buf, TAB);
            }

            count += helper.writeRaw(buf, String.valueOf(data));
            count += helper.write(buf, TAB);

        }

        count += helper.write(buf, LF);

        return count;
    }

    public void reset() {
        ctx.remove();
    }

    protected void setBufferWriter(BufferWriter writer) {
        this.bWriter = writer;
        this.bufferHelper = new BufferHelper(this.bWriter);
    }

    protected static class BufferHelper {

        private BufferWriter bWriter;

        public BufferHelper(BufferWriter writer) {
            bWriter = writer;
        }

        public String read(Context ctx, byte separator) {
            ByteBuf buf = ctx.getBuffer();
            char[] data = ctx.getData();
            int from = buf.readerIndex();
            int to = buf.writerIndex();
            int index = 0;
            boolean flag = false;

            for (int i = from; i < to; i++) {
                byte b = buf.readByte();

                if (b == separator) {
                    break;
                }

                if (index >= data.length) {
                    char[] data2 = new char[to - from];

                    System.arraycopy(data, 0, data2, 0, index);
                    data = data2;
                }

                char c = (char) (b & 0xFF);

                if (c > 127) {
                    flag = true;
                }

                if (c == '\\' && i + 1 < to) {
                    byte b2 = buf.readByte();

                    if (b2 == 't') {
                        c = '\t';
                        i++;
                    } else if (b2 == 'r') {
                        c = '\r';
                        i++;
                    } else if (b2 == 'n') {
                        c = '\n';
                        i++;
                    } else if (b2 == '\\') {
                        c = '\\';
                        i++;
                    } else {
                        // 后移
                        buf.readerIndex(i + 1);
                    }
                }

                data[index] = c;
                index++;
            }

            if (!flag) {
                return new String(data, 0, index);
            } else {
                byte[] ba = new byte[index];

                for (int i = 0; i < index; i++) {
                    ba[i] = (byte) (data[i] & 0xFF);
                }

                try {
                    return new String(ba, 0, index, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    return new String(ba, 0, index);
                }
            }
        }

        public int write(ByteBuf buf, byte b) {
            buf.writeByte(b);
            return 1;
        }

        public int write(ByteBuf buf, String str) {
            if (str == null) {
                str = "null";
            }

            byte[] data = str.getBytes();

            buf.writeBytes(data);
            return data.length;
        }

        public int writeRaw(ByteBuf buf, String str) {
            if (str == null) {
                str = "null";
            }

            byte[] data;

            try {
                data = str.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                data = str.getBytes();
            }

            return bWriter.writeTo(buf, data);
        }

    }

    public static class Context {

        private ByteBuf byteBuf;

        private char[]  data;

        public Context() {
            data = new char[4 * 1024 * 1024];
        }

        public ByteBuf getBuffer() {
            return byteBuf;
        }

        public char[] getData() {
            return data;
        }

        public Context setBuffer(ByteBuf buffer) {
            byteBuf = buffer;
            return this;
        }
    }

    /**
     * 线程安全日期助手类。 DateFormat不是线程安全的。
     * @author kris
     * @version $Id: PlainTextMessageCodec.java, v 0.1 2018年6月4日 下午4:20:50 kris Exp $
     */
    protected static class DateHelper {
        private BlockingQueue<SimpleDateFormat> formats = new ArrayBlockingQueue<>(20);

        private Map<String, Long>               map     = new ConcurrentHashMap<>();

        public String format(long timestamp) {
            SimpleDateFormat format = formats.poll();

            if (format == null) {
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            }

            try {
                return format.format(new Date(timestamp));
            } finally {
                if (formats.remainingCapacity() > 0) {
                    formats.offer(format);
                }
            }
        }

        public long parse(String str) {
            int len = str.length();
            String date = str.substring(0, 10);
            Long baseline = map.get(date);

            if (baseline == null) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    baseline = format.parse(date).getTime();
                    map.put(date, baseline);
                } catch (ParseException e) {
                    return -1;
                }
            }

            long time = baseline.longValue();
            long metric = 1;
            boolean millisecond = true;

            for (int i = len - 1; i > 10; i--) {
                char ch = str.charAt(i);

                if (ch >= '0' && ch <= '9') {
                    time += (ch - '0') * metric;
                    metric *= 10;
                } else if (millisecond) {
                    millisecond = false;
                } else {
                    metric = metric / 100 * 60;
                }
            }
            return time;
        }
    }

    protected static enum Policy {
                                  DEFAULT,

                                  WITHOUT_STATUS,

                                  WITH_DURATION;

        public static Policy getByMessageIdentifier(byte identifier) {
            switch (identifier) {
                case 't':
                    return WITHOUT_STATUS;
                case 'T':
                case 'A':
                    return WITH_DURATION;
                case 'E':
                case 'H':
                    return DEFAULT;
                default:
                    return DEFAULT;
            }
        }
    }

}
