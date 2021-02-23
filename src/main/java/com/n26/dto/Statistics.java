package com.n26.dto;

public class Statistics {

    private String sum;
    private String avg;
    private String max;
    private String min;
    private long count;

    private Statistics(Builder builder) {
        setSum(builder.sum);
        setAvg(builder.avg);
        setMax(builder.max);
        setMin(builder.min);
        setCount(builder.count);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public static final class Builder {
        private String sum;
        private String avg;
        private String max;
        private String min;
        private long count;

        private Builder() {
        }

        public Builder withSum(String sum) {
            this.sum = sum;
            return this;
        }

        public Builder withAvg(String avg) {
            this.avg = avg;
            return this;
        }

        public Builder withMax(String max) {
            this.max = max;
            return this;
        }

        public Builder withMin(String min) {
            this.min = min;
            return this;
        }

        public Builder withCount(long count) {
            this.count = count;
            return this;
        }

        public Statistics build() {
            return new Statistics(this);
        }
    }
}
