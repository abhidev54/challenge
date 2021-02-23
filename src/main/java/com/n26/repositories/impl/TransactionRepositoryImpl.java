package com.n26.repositories.impl;

import com.n26.AppConstants;
import com.n26.dto.Statistics;
import com.n26.dto.Transaction;
import com.n26.repositories.TransactionRepository;
import com.n26.util.StatisticUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class TransactionRepositoryImpl implements TransactionRepository {
    private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryImpl.class);
    private static final Map<Long, Statistics> transactions = new ConcurrentHashMap<>();
    private static final Map<Long, Statistics> expiredTransactions = new ConcurrentHashMap<>();

    private static Statistics latest = Statistics.newBuilder().withAvg("0.00").withCount(0)
            .withMax("0.00").withMin("0.00").withSum("0.00").build();
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void createTransaction(Transaction transaction) {
        lock.lock();
        try{
            StatisticUtil.addStatistics(latest, BigDecimal.valueOf(Double.parseDouble(transaction.getAmount())).setScale(AppConstants.SCALE, RoundingMode.HALF_UP));
        } finally {
            lock.unlock();
        }
        long timestamp = Instant.parse(transaction.getTimestamp()).atOffset(ZoneOffset.UTC).toEpochSecond();
        Statistics statistics = transactions.computeIfAbsent(timestamp, key -> Statistics.newBuilder().build());
        StatisticUtil.addStatistics(statistics, BigDecimal.valueOf(Double.parseDouble(transaction.getAmount())).setScale(AppConstants.SCALE, RoundingMode.HALF_UP));
    }

    @Override
    public Statistics getStatistics() {
        lock.lock();
        try {
            return StatisticUtil.cloneStatistics(latest);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteAllTransactions() {
        transactions.clear();
        latest = Statistics.newBuilder().withAvg("0.00").withCount(0)
                .withMax("0.00").withMin("0.00").withSum("0.00").build();
    }

    @Scheduled(cron = "* * * * * ?")
    private void removeOldTransactions() {
        long currentEpoch = Instant.now(Clock.systemUTC()).getEpochSecond();

        Statistics uncalculated = expiredTransactions.remove(currentEpoch);
        if (uncalculated != null) {
            lock.lock();
            try {
                StatisticUtil.addStatistics(latest, uncalculated);
            } finally {
                lock.unlock();
            }
            transactions.put(currentEpoch, uncalculated);
        }

        long before60seconds = currentEpoch - AppConstants.TRANSACTION_EXPIRED_OFFSET;
        Statistics statistics = transactions.remove(before60seconds);
        if (statistics == null) {
            return;
        }

        List<Double> maxMinAmountList = transactions.entrySet().parallelStream().map(e -> e.getValue())
                .flatMapToDouble(s -> Arrays.asList(s.getMin(), s.getMax()).parallelStream().mapToDouble(t -> Double.parseDouble(t)))
                .boxed().collect(Collectors.toList());

        lock.lock();
        try {
            StatisticUtil.remove(latest, statistics, maxMinAmountList);
        } finally {
            lock.unlock();
        }

        logger.info("Size: {}, count: {}, avg: {}, sum: {}, max: {}, min: {}", transactions.size(), latest.getCount(),
                latest.getAvg(), latest.getSum(), latest.getMax(), latest.getMin());
    }
}
