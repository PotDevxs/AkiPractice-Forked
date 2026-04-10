package aki.saki.practice.utils;

import lombok.Getter;
import lombok.AllArgsConstructor;

public class EloUtil {

    private static final KFactor[] K_FACTORS = {
            new KFactor(0, 1000, 25),
            new KFactor(1001, 1400, 20),
            new KFactor(1401, 1800, 15),
            new KFactor(1801, 2200, 10)
    };

    public static double getNewRating(int rating, int opponentRating, boolean won) {
        double probability = 1.0 / (1.0 + Math.pow(10, (double) (opponentRating - rating) / 400.0));
        double kFactor = EloUtil.getKFactor(rating);
        return rating + kFactor * ((won ? 1 : 0) - probability);
    }

    private static double getKFactor(int rating) {
        for (KFactor kFactor : K_FACTORS) {
            if (rating >= kFactor.getMin() && rating <= kFactor.getMax()) {
                return kFactor.getValue();
            }
        }
        return 10;
    }

    @Getter
    @AllArgsConstructor
    private static class KFactor {
        private final int min;
        private final int max;
        private final double value;
    }
}
