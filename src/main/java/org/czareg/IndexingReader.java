package org.czareg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.database.AbstractPagingItemReader;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@Slf4j
public class IndexingReader extends AbstractPagingItemReader<DbEvent> {

    public IndexingReader(int pageSize) {
        setPageSize(pageSize);
        setName("IndexingReader");
    }

    @Override
    protected void doReadPage() {

        if (results == null) {
            results = new ArrayList<>();
        } else {
            results.clear();
        }

        int page = getPage();

        int pageSize = getPageSize();
        int from = page * pageSize;
        int to = from + pageSize;

        if(from>=10_000){
            return;
        }
        List<DbEvent> customers = LongStream.range(from, to).mapToObj(DbEvent::new).toList();

        if (!CollectionUtils.isEmpty(customers)) {
            results.addAll(customers);
        }
    }
}