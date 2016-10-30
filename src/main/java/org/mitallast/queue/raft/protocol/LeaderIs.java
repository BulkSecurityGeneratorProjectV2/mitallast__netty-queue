package org.mitallast.queue.raft.protocol;

import org.mitallast.queue.common.stream.StreamInput;
import org.mitallast.queue.common.stream.StreamOutput;
import org.mitallast.queue.common.stream.Streamable;
import org.mitallast.queue.transport.DiscoveryNode;

import java.io.IOException;
import java.util.Optional;

public class LeaderIs implements Streamable {
    private final Optional<DiscoveryNode> leader;
    private final Optional<Streamable> msg;

    public LeaderIs(StreamInput stream) throws IOException {
        leader = Optional.ofNullable(stream.readStreamableOrNull(DiscoveryNode::new));
        if (stream.readBoolean()) {
            msg = Optional.of(stream.readStreamable());
        } else {
            msg = Optional.empty();
        }
    }

    public LeaderIs(Optional<DiscoveryNode> leader, Optional<Streamable> msg) {
        this.leader = leader;
        this.msg = msg;
    }

    public Optional<DiscoveryNode> getLeader() {
        return leader;
    }

    public Optional<Streamable> getMsg() {
        return msg;
    }

    @Override
    public void writeTo(StreamOutput stream) throws IOException {
        stream.writeStreamableOrNull(leader.orElse(null));
        if (msg.isPresent()) {
            stream.writeBoolean(true);
            stream.writeClass(msg.get().getClass());
            stream.writeStreamable(msg.get());
        } else {
            stream.writeBoolean(false);
        }
    }
}
