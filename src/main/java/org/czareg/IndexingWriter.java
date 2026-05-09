package org.czareg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import java.util.stream.Collectors;

@Slf4j
public class IndexingWriter implements ItemWriter<IndexEvent> {

    @Override
    public void write(Chunk<? extends IndexEvent> chunk) {
        String uuids = chunk.getItems()
                .stream()
                .map(IndexEvent::uuid)
                .collect(Collectors.joining(",", "[", "]"));
        log.info(uuids);
    }
}
