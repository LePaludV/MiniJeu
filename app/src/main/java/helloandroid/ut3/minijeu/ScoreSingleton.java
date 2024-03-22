package helloandroid.ut3.minijeu;

public class ScoreSingleton {
    int score;
    ScoreSingleton instance;

    private ScoreSingleton() {
        score = 0;
    }

    public ScoreSingleton getInstance() {
        if(instance == null) {
            instance = new ScoreSingleton();
        }
        return instance;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
