package org.czareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.jms.core.JmsTemplate;

@Slf4j
@RequiredArgsConstructor
public class IndexingJmsWriter implements ItemWriter<IndexEvent> {

    private final JmsTemplate jmsTemplate;

    @Override
    public void write(Chunk<? extends IndexEvent> chunk) {
        for (IndexEvent indexEvent : chunk) {
            jmsTemplate.convertAndSend(indexEvent.uuid());
        }
        log.info("Sent events {}", chunk.size());
    }
}
