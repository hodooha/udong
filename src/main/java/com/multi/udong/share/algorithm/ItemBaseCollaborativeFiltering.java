package com.multi.udong.share.algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemBaseCollaborativeFiltering {

    private final Map<Integer, Map<Integer, Double>> itemMemberPreferences;
    private final Map<Integer, Map<Integer, Double>> itemSimilarities;


    public ItemBaseCollaborativeFiltering(Map<Integer, Map<Integer, Double>> itemMemberPreferences) {
        this.itemMemberPreferences = itemMemberPreferences;
        this.itemSimilarities = new HashMap<>();
        calculateItemSimilarities();
    }


    private void calculateItemSimilarities() {
        for (Integer item1 : itemMemberPreferences.keySet()) {
            itemSimilarities.putIfAbsent(item1, new HashMap<>());
            for (Integer item2 : itemMemberPreferences.keySet()) {
                if (Objects.equals(item1, item2)) continue;

                double similarity = calculateCosineSimilarity(itemMemberPreferences.get(item1), itemMemberPreferences.get(item2));
                itemSimilarities.get(item1).put(item2, similarity);
            }
        }
    }

    // 아이템 간 코사인 유사도 계산
    private double calculateCosineSimilarity(Map<Integer, Double> prefs1, Map<Integer, Double> prefs2) {

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Integer member : prefs1.keySet()) {
            if (prefs2.containsKey(member)) {
                dotProduct += prefs1.get(member) * prefs2.get(member);
                norm1 += Math.pow(prefs1.get(member), 2);
                norm2 += Math.pow(prefs2.get(member), 2);
            }
        }

        // 유사도 0~1 범위의 값.
        return (norm1 == 0 || norm2 == 0) ? 0 : dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // 로그인한 유저의 찜, 대여 및 나눔 요청을 반영한 유사 물건 및 유사도 점수 조회
    public Map<Integer, Double> recommend(Map<Integer, Double> memberRatings) {

        Map<Integer, Double> recommendations = new HashMap<>();

        for (Integer item : memberRatings.keySet()) {
            Map<Integer, Double> similarities = itemSimilarities.get(item);
            if (similarities != null) {
                for (Map.Entry<Integer, Double> entry : similarities.entrySet()) {
                    int similarItem = entry.getKey();
                    double similarity = entry.getValue();
                    double currentScore = recommendations.getOrDefault(similarItem, 0.0);
                    recommendations.put(similarItem, currentScore + similarity * memberRatings.get(item));
                }
            }
        }

        return recommendations;
    }
}
