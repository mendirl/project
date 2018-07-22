package org.mendirl.service.cube.activepivot.source;

import com.qfs.msg.IListenerKey;
import com.qfs.msg.IMessageChannel;
import com.qfs.msg.ISource;

import java.util.Collection;
import java.util.List;

/**
 * Source of trades (randomly generated trades)
 *
 * @author Quartet FS
 */
public class DataSource implements ISource<String, Object> {

    /**
     * Forex topic
     */
    public static final String TOPIC_FOREX = "Forex";

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<String> getTopics() {
        return null;
    }

    @Override
    public void fetch(Collection<? extends IMessageChannel<String, Object>> collection) {

    }

    @Override
    public IListenerKey listen(IMessageChannel<String, Object> iMessageChannel) {
        return null;
    }
}
