/**
 * CAT的小老鼠
 * Copyright (c) 1995-2018 All Rights Reserved.
 */
package com.mouse.message.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.lookup.logging.LogEnabled;
import org.unidal.lookup.logging.Logger;

import com.mouse.configuration.ClientConfigManager;
import com.mouse.configuration.NetworkInterfaceManager;
import com.mouse.configuration.client.entity.Domain;
import com.mouse.message.ForkedTransaction;
import com.mouse.message.Message;
import com.mouse.message.TaggedTransaction;
import com.mouse.message.Transaction;
import com.mouse.message.io.MessageSender;
import com.mouse.message.io.TransportManager;
import com.mouse.message.spi.MessageManager;
import com.mouse.message.spi.MessageTree;

/**
 * 默认消息管理器
 * @author kris
 * @version $Id: DefaultMessageManager.java, v 0.1 2018年6月1日 下午3:59:23 kris Exp $
 */
public class DefaultMessageManager extends ContainerHolder implements MessageManager, Initializable, LogEnabled {

    @Inject
    private ClientConfigManager            configManager;

    @Inject
    private TransportManager               transportManager;

    @Inject
    private MessageIdFactory               factory;

    // 不使用静态修饰符，因为MessageManager被配置为单例
    private ThreadLocal<Context>           context      = new ThreadLocal<>();

    private long                           throttleTimes;

    private Domain                         domain;

    private String                         hostName;

    private boolean                        firstMessage = true;

    private TransactionHelper              validator    = new TransactionHelper();

    private Map<String, TaggedTransaction> taggerdTransactions;

    private Logger                         logger;

    @Override
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void initialize() throws InitializationException {
        domain = configManager.getDomain();
        hostName = NetworkInterfaceManager.INSTANCE.getLocalHostName();

        if (domain.getIp() == null) {
            domain.setIp(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
        }

        // 初始化domain和ip地址
        try {
            factory.initialize(domain.getId());
        } catch (IOException e) {
            throw new InitializationException("初始化MessageIdFactory时出错!", e);
        }

        // 初始化标记事务缓存
        final int size = configManager.getTaggedTransactionCacheSize();

        taggerdTransactions = new LinkedHashMap<String, TaggedTransaction>(size * 4 / 3 + 1, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Entry<String, TaggedTransaction> eldest) {
                return size() >= size;
            }
        };
    }

    @Override
    public void add(Message message) {
        Context ctx = getContext();

        if (ctx != null) {
            ctx.add(message);
        }
    }

    @Override
    public void end(Transaction transaction) {
        Context ctx = getContext();

        if (ctx != null && transaction.isStandalone()) {
            if (ctx.end(this, transaction)) {
                context.remove();
            }
        }
    }

    @Override
    public Transaction getPeekTransaction() {
        Context ctx = getContext();

        if (ctx != null) {
            return ctx.peekTransaction(this);
        } else {
            return null;
        }
    }

    @Override
    public MessageTree getThreadLocalMessageTree() {
        Context ctx = context.get();

        if (ctx == null) {
            setup();
        }
        ctx = context.get();

        return ctx.tree;
    }

    @Override
    public boolean hasContext() {
        return context.get() != null;
    }

    @Override
    public boolean isMessageEnabled() {
        return domain != null && domain.isEnabled() && context.get() != null && configManager.isMouseEnabled();
    }

    @Override
    public boolean isMouseEnabled() {
        return domain != null && domain.isEnabled() && configManager.isMouseEnabled();
    }

    @Override
    public boolean isTraceMode() {
        Context content = getContext();

        if (content != null) {
            return content.isTraceMode();
        } else {
            return false;
        }
    }

    @Override
    public void reset() {
        // 销毁当前线程本地数据
        Context ctx = context.get();

        if (ctx != null) {
            if (ctx.totalDurationInMicros == 0) {
                ctx.stack.clear();
                ctx.knownExceptions.clear();
                context.remove();
            } else {
                ctx.knownExceptions.clear();
            }
        }
    }

    @Override
    public void setTradeMode(boolean traceMode) {
        Context context = getContext();

        if (context != null) {
            context.setTraceMode(traceMode);
        }

    }

    @Override
    public void setup() {
        Context ctx;

        if (domain != null) {
            ctx = new Context(domain.getId(), hostName, domain.getIp());
        } else {
            ctx = new Context("Unknown", hostName, "");
        }

        context.set(ctx);
    }

    @Override
    public void start(Transaction transaction, boolean forked) {
        Context ctx = getContext();

        if (ctx != null) {
            ctx.start(transaction, forked);

            if (transaction instanceof TaggedTransaction) {
                TaggedTransaction tt = (TaggedTransaction) transaction;

                taggerdTransactions.put(tt.getTag(), tt);
            }
        } else if (firstMessage) {
            firstMessage = false;
            logger.warn("MOUST客户端未启动，因为它尚未初始化");
        }
    }

    @Override
    public void bind(String tag, String title) {
        TaggedTransaction t = taggerdTransactions.get(tag);

        if (t != null) {
            MessageTree tree = getThreadLocalMessageTree();
            String messageId = tree.getMessageId();

            if (messageId == null) {
                messageId = nextMessageId();
                tree.setMessageId(messageId);
            }
            if (tree != null) {
                t.start();
                t.bind(tag, messageId, title);
            }
        }
    }

    @Override
    public String getDomain() {
        return domain.getId();
    }

    public ClientConfigManager getConfigManager() {
        return configManager;
    }

    public void seteMetricType(String metricType) {
    }

    public String getMetricType() {
        return "";
    }

    public void linkAsRunAway(DefaultForkedTransaction transaction) {
        Context ctx = getContext();
        if (ctx != null) {
            ctx.linkAsRunAway(transaction);
        }
    }

    private Context getContext() {
        Context ctx = context.get();

        if (ctx != null) {
            return ctx;
        } else {
            if (domain != null) {
                ctx = new Context(domain.getId(), hostName, domain.getIp());
            } else {
                ctx = new Context("Unknown", hostName, "");
            }

            context.set(ctx);
            return ctx;
        }
    }

    public void flush(MessageTree tree) {
        if (tree.getMessageId() == null) {
            tree.setMessageId(nextMessageId());
        }

        MessageSender sender = transportManager.getSender();

        if (sender != null && isMessageEnabled()) {
            sender.send(tree);

            reset();
        } else {
            throttleTimes++;

            if (throttleTimes % 10000 == 0 || throttleTimes == 1) {
                logger.info("Mouse消息被抑制！次数：" + throttleTimes);
            }
        }

    }

    public String nextMessageId() {
        return factory.getNextId();
    }

    boolean shouldLog(Throwable e) {
        Context ctx = context.get();

        if (ctx != null) {
            return ctx.shouldLog(e);
        } else {
            return true;
        }
    }

    class Context {

        private MessageTree        tree;

        private Stack<Transaction> stack;

        private int                length;

        private boolean            traceMode;

        private long               totalDurationInMicros; // 截断消息

        private Set<Throwable>     knownExceptions;

        public Context(String domain, String hostName, String ipAddress) {
            tree = new DefaultMessageTree();
            stack = new Stack<>();

            Thread thread = Thread.currentThread();
            String groupName = thread.getThreadGroup().getName();

            tree.setThreadGroupName(groupName);
            tree.setThreadId(String.valueOf(thread.getId()));
            tree.setThreadName(thread.getName());

            tree.setDomain(domain);
            tree.setHostName(hostName);
            tree.setIpAddress(ipAddress);
            length = 1;
            knownExceptions = new HashSet<>();
        }

        public boolean shouldLog(Throwable e) {
            if (knownExceptions == null) {
                knownExceptions = new HashSet<>();
            }

            if (knownExceptions.contains(e)) {
                return false;
            } else {
                knownExceptions.add(e);
                return true;
            }
        }

        public void linkAsRunAway(DefaultForkedTransaction transaction) {
            validator.linkAsRunAway(transaction);
        }

        public void start(Transaction transaction, boolean forked) {
            if (!stack.isEmpty()) {
                // 不要从父事务到分支事务做强引用
                // 相反，通过linkAsRunAway()创建分支事务的软引用
                // 这样就不需要在父进程和子进程间进行同步
                // 尽管有其他线程，两个线程都可以随时complete()
                if (!(transaction instanceof ForkedTransaction)) {
                    Transaction parent = stack.peek();
                    addTransactionChild(transaction, parent);
                }
            } else {
                tree.setMessage(transaction);
            }

            if (!forked) {
                stack.push(transaction);
            }
        }

        public void setTraceMode(boolean traceMode) {
            this.traceMode = traceMode;
        }

        public boolean isTraceMode() {
            return traceMode;
        }

        /**
         * 返回true表示事务已被刷新。
         * @param manager
         * @param transaction
         * @return
         */
        public boolean end(DefaultMessageManager manager, Transaction transaction) {
            if (!stack.isEmpty()) {
                Transaction current = stack.pop();

                if (transaction == current) {
                    validator.validate(stack.isEmpty() ? null : stack.peek(), current);
                } else {
                    while (transaction != current && !stack.empty()) {
                        validator.validate(stack.peek(), current);

                        current = stack.pop();
                    }
                }

                if (stack.isEmpty()) {
                    MessageTree tree = this.tree.copy();

                    this.tree.setMessageId(null);
                    this.tree.setMessage(null);

                    if (totalDurationInMicros > 0) {
                        adjustForTruncatedTransaction((Transaction) tree.getMessage());
                    }

                    manager.flush(tree);
                    return true;
                }
            }
            return false;
        }

        private void adjustForTruncatedTransaction(Transaction root) {
            DefaultEvent next = new DefaultEvent("TruncatedTransaction", "TotalDuration");
            long actualDurationInMicros = totalDurationInMicros + root.getDurationInMicros();

            next.addData(String.valueOf(actualDurationInMicros));
            next.setStatus(Message.SUCCESS);
            root.addChild(next);

            totalDurationInMicros = 0;
        }

        public Transaction peekTransaction(DefaultMessageManager defaultMessageManager) {
            if (stack.isEmpty()) {
                return null;
            } else {
                return stack.peek();
            }
        }

        public void add(Message message) {
            if (stack.isEmpty()) {
                MessageTree tree = this.tree.copy();

                tree.setMessage(message);
                flush(tree);
            } else {
                Transaction parent = stack.peek();

                addTransactionChild(message, parent);
            }
        }

        private void addTransactionChild(Message message, Transaction transaction) {
            long treePeriod = trimToHour(tree.getMessage().getTimestamp());
            long messagePeriod = trimToHour(message.getTimestamp() - 10 * 1000L);

            if (treePeriod < messagePeriod || length > configManager.getMaxMessageLength()) {
                validator.truncateAndFlush(this, message.getTimestamp());
            }

            transaction.addChild(message);
            length++;
        }

        private long trimToHour(long timestamp) {
            return timestamp - timestamp % (3600 * 1000L);
        }

    }

    class TransactionHelper {

        public void truncateAndFlush(Context ctx, long timestamp) {
            MessageTree tree = ctx.tree;
            Stack<Transaction> stack = ctx.stack;
            Message message = tree.getMessage();

            if (message instanceof DefaultTransaction) {
                String id = tree.getMessageId();

                if (id == null) {
                    id = nextMessageId();
                    tree.setMessageId(id);
                }

                String rootId = tree.getRootMessageId();
                String childId = nextMessageId();
                DefaultTransaction source = (DefaultTransaction) message;
                DefaultTransaction target = new DefaultTransaction(source.getType(), source.getName(), DefaultMessageManager.this);

                target.setTimestamp(source.getTimestamp());
                target.setDurationInMicros(source.getDurationInMicros());
                target.addData(source.getData().toString());
                target.setStatus(Message.SUCCESS);

                migrateMessage(stack, source, target, 1);

                for (int i = stack.size() - 1; i >= 0; i--) {
                    DefaultTransaction t = (DefaultTransaction) stack.get(i);

                    t.setTimestamp(timestamp);
                    t.setDurationStart(System.nanoTime());
                }

                DefaultEvent next = new DefaultEvent("RemoteCall", "Next");

                next.addData(childId);
                next.setStatus(Message.SUCCESS);
                target.addChild(next);

                MessageTree t = tree.copy();

                t.setMessage(target);

                ctx.tree.setMessageId(childId);
                ctx.tree.setParentMessageId(id);
                ctx.tree.setRootMessageId(rootId != null ? rootId : id);

                ctx.length = stack.size();
                ctx.totalDurationInMicros = ctx.totalDurationInMicros + target.getDurationInMicros();

                flush(t);
            }
        }

        public void validate(Transaction parent, Transaction transaction) {
            if (transaction.isStandalone()) {
                List<Message> children = transaction.getChildren();
                int len = children.size();

                for (int i = 0; i < len; i++) {
                    Message message = children.get(i);

                    if (message instanceof Transaction) {
                        validate(transaction, (Transaction) message);
                    }
                }

                if (!transaction.isCompleted() && transaction instanceof DefaultTransaction) {
                    // 缺少事务结束，记录异常事件，以便开发人员可以修复代码
                    markAsNotCompleted((DefaultTransaction) transaction);
                }
            } else if (!transaction.isCompleted()) {
                if (transaction instanceof DefaultForkedTransaction) {
                    // 由于分支事务未完成，将其链接未失控消息
                    linkAsRunAway((DefaultForkedTransaction) transaction);
                } else if (transaction instanceof DefaultTaggedTransaction) {
                    // 由于分支事务未完成，将其链接未失控消息
                    markAsRunAway(parent, (DefaultTaggedTransaction) transaction);
                }
            }
        }

        private void markAsRunAway(Transaction parent, DefaultTaggedTransaction transaction) {
            if (!transaction.hasChildren()) {
                transaction.addData("RunAway");
            }

            transaction.setStatus(Message.SUCCESS);
            transaction.setStandalone(true);
            transaction.complete();
        }

        private void linkAsRunAway(DefaultForkedTransaction transaction) {
            DefaultEvent event = new DefaultEvent("RemoteCall", "RunAway");

            event.addData(transaction.getForkedMessageId(), transaction.getType() + ":" + transaction.getName());
            event.setTimestamp(transaction.getTimestamp());
            event.setStatus(Message.SUCCESS);
            event.setCompleted(true);
            transaction.setStandalone(true);

            add(event);
        }

        private void markAsNotCompleted(DefaultTransaction transaction) {
            DefaultEvent event = new DefaultEvent("mouse", "BadInstrument");

            event.setStatus("TransactionNotCompleted");
            event.setCompleted(true);
            transaction.addChild(event);
            transaction.setCompleted(true);
        }

        private void migrateMessage(Stack<Transaction> stack, Transaction source, Transaction target, int level) {
            Transaction current = level < stack.size() ? stack.get(level) : null;
            boolean shouldKeep = true;

            for (Message child : source.getChildren()) {
                if (child != current) {
                    target.addChild(child);
                } else {
                    DefaultTransaction cloned = new DefaultTransaction(current.getType(), current.getName(), DefaultMessageManager.this);

                    cloned.setTimestamp(current.getTimestamp());
                    cloned.setDurationInMicros(current.getDurationInMicros());
                    cloned.addData(current.getData().toString());
                    cloned.setStatus(Message.SUCCESS);

                    target.addChild(cloned);
                    migrateMessage(stack, current, cloned, level + 1);
                    shouldKeep = true;
                }
            }

            source.getChildren().clear();

            if (shouldKeep) {
                source.addChild(current);
            }
        }

    }

}
