package org.mitallast.queue.action.queue.get;

import com.google.inject.Inject;
import org.mitallast.queue.action.AbstractAction;
import org.mitallast.queue.common.concurrent.Listener;
import org.mitallast.queue.common.settings.Settings;
import org.mitallast.queue.queue.QueueMessage;
import org.mitallast.queue.queue.transactional.TransactionalQueueService;
import org.mitallast.queue.queues.QueueMissingException;
import org.mitallast.queue.queues.transactional.TransactionalQueuesService;
import org.mitallast.queue.transport.TransportController;

import java.io.IOException;

public class GetAction extends AbstractAction<GetRequest, GetResponse> {

    private final TransactionalQueuesService queuesService;

    @Inject
    public GetAction(Settings settings, TransportController controller, TransactionalQueuesService queuesService) {
        super(settings, controller);
        this.queuesService = queuesService;
    }

    @Override
    protected void executeInternal(GetRequest request, Listener<GetResponse> listener) {
        if (!queuesService.hasQueue(request.queue())) {
            listener.onFailure(new QueueMissingException(request.queue()));
        }
        TransactionalQueueService queueService = queuesService.queue(request.queue());
        try {
            QueueMessage message = queueService.get(request.uuid());
            listener.onResponse(GetResponse.builder()
                .setMessage(message)
                .build());
        } catch (IOException e) {
            listener.onFailure(e);
        }
    }
}
