package org.mitallast.queue.blob.protocol;

import org.mitallast.queue.common.stream.StreamInput;
import org.mitallast.queue.common.stream.StreamOutput;
import org.mitallast.queue.common.stream.Streamable;
import org.mitallast.queue.transport.DiscoveryNode;

import java.io.IOException;

public class PutBlobResourceResponse implements Streamable {
    private final long id;
    private final DiscoveryNode node;
    private final String key;
    private final boolean stored;

    public PutBlobResourceResponse(long id, DiscoveryNode node, String key, boolean stored) {
        this.id = id;
        this.node = node;
        this.key = key;
        this.stored = stored;
    }

    public PutBlobResourceResponse(StreamInput stream) throws IOException {
        this.id = stream.readLong();
        this.node = stream.readStreamable(DiscoveryNode::new);
        this.key = stream.readText();
        this.stored = stream.readBoolean();
    }

    public long getId() {
        return id;
    }

    public DiscoveryNode getNode() {
        return node;
    }

    public String getKey() {
        return key;
    }

    public boolean isStored() {
        return stored;
    }

    @Override
    public void writeTo(StreamOutput stream) throws IOException {
        stream.writeLong(id);
        stream.writeStreamable(node);
        stream.writeText(key);
        stream.writeBoolean(stored);
    }
}
