package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.util.TimeUtils;

public interface ScoreFunction<T> {

    public static final ScoreFunction<Float> TIME_FANCY = new ScoreFunction<Float>() {

        public String apply(Float value) {
            if (value >= 60) {
                return (TimeUtils.getMMSS(value.intValue()));
            } else {
                return (Math.round(10.0D * value) / 10.0D + "s");
            }
        }

    };

    public static final ScoreFunction<Float> TIME_SIMPLE = new ScoreFunction<Float>() {

        public String apply(Float value) {
            return (TimeUtils.getMMSS(value.intValue()));
        }

    };

    public String apply(T t);

}