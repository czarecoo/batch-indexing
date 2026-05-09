package org.czareg;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class IndexingProcessor implements ItemProcessor<DbEvent, IndexEvent> {

    @Override
    public @Nullable IndexEvent process(DbEvent item) throws Exception {
        return new IndexEvent(String.valueOf(item.id()));
    }
}
