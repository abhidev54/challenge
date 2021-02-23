package com.n26.util;

import com.n26.AppConstants;
import com.n26.dto.Statistics;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

public class StatisticUtil {

    public static void addStatistics(Statistics to, BigDecimal amount) {
        to.setCount(to.getCount() + 1);
        String dAmount = amount.toString();
        if (to.getCount() == 1) {
            to.setSum(dAmount);
            to.setAvg(dAmount);
            to.setMin(dAmount);
            to.setMax(dAmount);
        } else {
            BigDecimal sum = BigDecimal.valueOf(Double.parseDouble(to.getSum())).add(amount).setScale(AppConstants.SCALE,
                    RoundingMode.HALF_UP);
            to.setSum(sum.toString());
            to.setAvg(sum.divide(BigDecimal.valueOf(to.getCount()), RoundingMode.HALF_UP).toString());
            to.setMin(String.valueOf(BigDecimal.valueOf(Math.min(Double.parseDouble(to.getMin()), Double.parseDouble(dAmount))).setScale(AppConstants.SCALE,
                    RoundingMode.HALF_UP)));
            to.setMax(String.valueOf(BigDecimal.valueOf(Math.max(Double.parseDouble(to.getMax()), Double.parseDouble(dAmount))).setScale(AppConstants.SCALE,
                    RoundingMode.HALF_UP)));
        }
    }

    public static Statistics cloneStatistics(Statistics statistics) {
        return Statistics.newBuilder().withAvg(statistics.getAvg()).withCount(statistics.getCount())
                .withMax(statistics.getMax()).withMin(statistics.getMin()).withSum(statistics.getSum()).build();
    }

    public static void addStatistics(Statistics to, Statistics from) {
        to.setCount(to.getCount() + from.getCount());
        if (to.getCount() == from.getCount()) {
            to.setSum(String.valueOf(BigDecimal.valueOf(Double.parseDouble(from.getSum())).setScale(AppConstants.SCALE, RoundingMode.HALF_UP)));
            to.setAvg(String.valueOf(BigDecimal.valueOf((Double.parseDouble(from.getAvg()))).setScale(AppConstants.SCALE, RoundingMode.HALF_UP)));
            to.setMin(String.valueOf(BigDecimal.valueOf((Double.parseDouble(from.getMin()))).setScale(AppConstants.SCALE, RoundingMode.HALF_UP)));
            to.setMax(String.valueOf(BigDecimal.valueOf((Double.parseDouble(from.getMax()))).setScale(AppConstants.SCALE, RoundingMode.HALF_UP)));
        } else {
            BigDecimal sum = BigDecimal.valueOf(Double.parseDouble(to.getSum())).add(BigDecimal.valueOf(Double.parseDouble(from.getSum())))
                    .setScale(AppConstants.SCALE, RoundingMode.HALF_UP);
            to.setSum(sum.toString());
            to.setAvg(sum.divide(BigDecimal.valueOf(to.getCount()), RoundingMode.HALF_UP).toString());
            to.setMin(String.valueOf(BigDecimal.valueOf(Math.min(Double.parseDouble(to.getMin()), Double.parseDouble(from.getMin()))).setScale(AppConstants.SCALE, RoundingMode.HALF_UP)));
            to.setMax(String.valueOf(BigDecimal.valueOf(Math.max(Double.parseDouble(to.getMax()), Double.parseDouble(from.getMax()))).setScale(AppConstants.SCALE, RoundingMode.HALF_UP)));
        }
    }

    public static void remove(Statistics from, Statistics to, List<Double> maxMinAmountList) {
        BigDecimal sum = BigDecimal.valueOf(Double.parseDouble(from.getSum())).subtract(BigDecimal.valueOf(Double.parseDouble(to.getSum())))
                .setScale(AppConstants.SCALE, RoundingMode.HALF_UP);
        from.setCount(from.getCount() - to.getCount());
        from.setSum(sum.toString());

        if (from.getCount() == 1) {
            to.setAvg(String.valueOf(BigDecimal.valueOf((Double.parseDouble(from.getAvg()))).setScale(AppConstants.SCALE, RoundingMode.HALF_UP)));
            to.setMin(String.valueOf(BigDecimal.valueOf((Double.parseDouble(from.getMin()))).setScale(AppConstants.SCALE, RoundingMode.HALF_UP)));
            to.setMax(String.valueOf(BigDecimal.valueOf((Double.parseDouble(from.getMax()))).setScale(AppConstants.SCALE, RoundingMode.HALF_UP)));
        } else if (from.getCount() > 0) {
            from.setAvg(
                    sum.divide(BigDecimal.valueOf(from.getCount()), AppConstants.SCALE, RoundingMode.HALF_UP)
                            .toString());
            Supplier<DoubleStream> doubleStreamSupplier = () -> maxMinAmountList.parallelStream().mapToDouble(d -> d);
            from.setMin(String.valueOf(doubleStreamSupplier.get().min().orElse(0.00)));
            from.setMax(String.valueOf(doubleStreamSupplier.get().max().orElse(0.00)));
        } else {
            from.setAvg("0.00");
            from.setMin("0.00");
            from.setMax("0.00");
        }
    }
}
