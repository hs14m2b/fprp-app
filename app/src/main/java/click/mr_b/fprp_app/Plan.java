package click.mr_b.fprp_app;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Plan {

    @PrimaryKey(autoGenerate = true)
    private int id;
    public int getId(){return id;}
    public void setId(int id) {this.id = id;}

    @ColumnInfo(name = "plan_name")
    public String planName;
    public String getPlanName() {
        return planName;
    }
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    @ColumnInfo(name = "point1")
    public String point1;
    public String getPoint1(){return point1;}
    public void setPoint1(String point1){this.point1 = point1;}

    @ColumnInfo(name = "point2")
    public String point2;
    public String getPoint2(){return point2;}
    public void setPoint2(String point2){this.point2 = point2;}

    @ColumnInfo(name = "point3")
    public String point3;
    public String getPoint3(){return point3;}
    public void setPoint3(String point3){this.point3 = point3;}

    @ColumnInfo(name = "point4")
    public String point4;
    public String getPoint4(){return point4;}
    public void setPoint4(String point4){this.point4 = point4;}

    @ColumnInfo(name = "point5")
    public String point5;
    public String getPoint5(){return point5;}
    public void setPoint5(String point5){this.point5 = point5;}

    @ColumnInfo(name = "question1")
    public String question1;
    public String getQuestion1(){return question1;}
    public void setQuestion1(String question1){this.question1 = question1;}

    @ColumnInfo(name = "question2")
    public String question2;
    public String getQuestion2(){return question2;}
    public void setQuestion2(String question2){this.question2 = question2;}

    @ColumnInfo(name = "question3")
    public String question3;
    public String getQuestion3(){return question3;}
    public void setQuestion3(String question3){this.question3 = question3;}

    @ColumnInfo(name = "question4")
    public String question4;
    public String getQuestion4(){return question4;}
    public void setQuestion4(String question4){this.question4 = question4;}

    @ColumnInfo(name = "question5")
    public String question5;
    public String getQuestion5(){return question5;}
    public void setQuestion5(String question5){this.question5 = question5;}

    @ColumnInfo(name = "question6")
    public String question6;
    public String getQuestion6(){return question6;}
    public void setQuestion6(String question6){this.question6 = question6;}

    @ColumnInfo(name = "encstatus")
    public int encstatus;
    public int getEncstatus(){return encstatus;}
    public void setEncstatus(int encstatus){this.encstatus = encstatus;}

    Plan(){

        planName = "";
        point1 = "";
        point2 = "";
        point3 = "";
        point4 = "";
        point5 = "";
        question1 = "";
        question2 = "";
        question4 = "";
        question3 = "";
        question5 = "";
        question6 = "";
        encstatus = 0;
    }

    Plan(String planName){
        this.planName = planName;
        point1 = "";
        point2 = "";
        point3 = "";
        point4 = "";
        point5 = "";
        question1 = "";
        question2 = "";
        question4 = "";
        question3 = "";
        question5 = "";
        question6 = "";
        encstatus = 0;
    }

    @Override
    public String toString()
    {
        return this.getId() + "|" +
                this.getPlanName() + "|" +
                this.getQuestion1() + "|" +
                this.getQuestion2() + "|" +
                this.getQuestion3() + "|" +
                this.getQuestion4() + "|" +
                this.getQuestion5() + "|" +
                this.getQuestion6() + "|" +
                this.getPoint1() + "|" +
                this.getPoint2() + "|" +
                this.getPoint3() + "|" +
                this.getPoint4() + "|" +
                this.getPoint5();

    }
}
