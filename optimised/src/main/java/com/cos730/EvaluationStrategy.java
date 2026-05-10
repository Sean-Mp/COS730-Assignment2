package com.cos730;

import java.util.List;

public interface EvaluationStrategy {
    EvaluationDecision evaluate(List<Integer> scores);
}
