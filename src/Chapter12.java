import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class Chapter12 {
    public static void main(String[] args){
        System.out.println("chapter12");
    }
    public class Lecture{
        private int pass;
        private String title;
        private List<Integer> scores = new ArrayList<>();
        public Lecture(String title, int pass, List<Integer> scores){
            this.title = title;
            this.pass = pass;
            this.scores = scores;
        }
        public double average(){
            return scores.stream()
                    .mapToInt(Integer::intValue)
                    .average().orElse(0);
        }
        public String stats(){
            return String.format("Title: %s, Evaluation Method: %s", title, getEvaluationMethod());
        }
        public String getEvaluationMethod(){
            return "Pass or Fail";
        }
        public List<Integer> getScores(){
            return Collections.unmodifiableList(scores);
        }
        public String evaluate(){
            return String.format("Pass:%d",passCount(),failCount());
        }
        public long passCount(){
            return scores.stream().filter(score -> score >= pass).count();
        }
        public long failCount(){
            return scores.size() - passCount();
        }
    }
    public class Grade{
        private String name;
        private int upper, lower;
        private Grade(String name, int upper, int lower){
            this.name = name;
            this.upper = upper;
            this.lower = lower;
        }
        public String getName(){
            return name;
        }
        public boolean isName(String name){
            return this.name.equals(name);
        }
        public boolean include(int score){
            return score >= lower && score <= upper;
        }
    }
    public class GradeLecture extends Lecture{
        private List<Grade> grades;
        @Override
        public String evaluate(){
            return super.evaluate() + ", "+gradesStatistics();
        }
        private String gradesStatistics(){
            return grades.stream()
                    .map(grade -> format(grade))
                    .collect(joining(" "));
        }
        @Override
        public String getEvaluationMethod(){
            return "Grade";
        }
        private String format(Grade grade){
            return String.format("%s:%d", grade.getName(), gradeCount(grade));
        }
        private long gradeCount(Grade grade){
            return getScores().stream()
                    .filter(grade::include)
                    .count();
        }
        public GradeLecture(String name, int pass, List<Grade> grades, List<Integer> scores){
            super(name, pass, scores);
            this.grades = grades;
        }
        public double average(String gradeName){
            return grades.stream()
                    .filter(each -> each.isName(gradeName))
                    .findFirst()
                    .map(this::gradeAverage)
                    .orElse(0d);
        }
        private double gradeAverage(){
            return getScores().stream()
                    .filter(grade::include)
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0);
        }
    }
    public class Professor{
        private String name;
        private Lecture lecture;
        public Professor(String name, Lecture lecture){
            this.name = name;
            this.lecture = lecture;
        }
        public String compileStatistics(){
            return String.format("[%s] %s - Avg: %.1f", name, lecture.evaluate(), lecture.average());
        }
    }
}
