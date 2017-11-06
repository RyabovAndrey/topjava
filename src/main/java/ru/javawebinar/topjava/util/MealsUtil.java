package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> mealList = Arrays.asList(
                new Meal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new Meal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new Meal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new Meal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
        List<MealWithExceed> filteredWithExceeded = getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        filteredWithExceeded.forEach(System.out::println);
        System.out.println();
        List<MealWithExceed> filteredWithExceededByCycle = getFilteredWithExceededByCycle(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        filteredWithExceededByCycle.forEach(System.out::println);
        System.out.println();
        List<MealWithExceed> filteredWithExceededByCycleLambda = getFilteredWithExceededByCycleLambda(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        filteredWithExceededByCycleLambda.forEach(System.out::println);

    }

    public static List<MealWithExceed>  getFilteredWithExceeded(List<Meal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = mealList.stream().collect(Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories)));
        return mealList.stream()
                .filter(meal->TimeUtil.isBetween(meal.getTime(),startTime,endTime))
                .map(meal->new MealWithExceed(meal.getDateTime(),meal.getDescription(),meal.getCalories(),
                        caloriesSumByDate.get(meal.getDate())>caloriesPerDay))
                .collect(Collectors.toList());

    }

    public static List<MealWithExceed> getFilteredWithExceededByCycle(List<Meal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map <LocalDate,Integer> caloriesSumByDate=new HashMap<>();
        for (Meal meal: mealList) {
            LocalDate mealDate=meal.getDate();
            caloriesSumByDate.put(mealDate,caloriesSumByDate.getOrDefault(mealDate,0)+meal.getCalories());
        }

        List<MealWithExceed> mealExceeded=new ArrayList<>();
        for (Meal meal: mealList) {
            if (TimeUtil.isBetween(meal.getTime(),startTime,endTime)) {
                mealExceeded.add(createMealWithExceed(meal, caloriesSumByDate.get(meal.getDate())>caloriesPerDay));
            }
        }

        return mealExceeded;
    }

    public static List<MealWithExceed> getFilteredWithExceededByCycleLambda(List<Meal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map <LocalDate,Integer> caloriesSumByDate=new HashMap<>();
        mealList.forEach(meal -> caloriesSumByDate.merge(meal.getDate(),meal.getCalories(),Integer::sum));

        List<MealWithExceed> mealExceeded=new ArrayList<>();
        mealList.forEach(meal -> {
            if (TimeUtil.isBetween(meal.getTime(),startTime,endTime)) {
                mealExceeded.add(createMealWithExceed(meal,caloriesSumByDate.get(meal.getDate())>caloriesPerDay));
            }
        });

        return mealExceeded;
    }

    private static MealWithExceed createMealWithExceed(Meal meal, boolean exceeded) {
        return new MealWithExceed(meal.getDateTime(),meal.getDescription(), meal.getCalories(),exceeded);
    }
}
