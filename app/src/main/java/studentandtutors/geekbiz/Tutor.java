package studentandtutors.geekbiz;

public class Tutor {

    private final int i;
    private String aid;
    private String fname;
    private String lname;
    private String edlevel;
    private int Wage;
    private String city;

    public Tutor(String aid, String fname, String lname, String edlevel, int wage, String city, int i) {
        this.aid = aid;
        this.fname = fname;
        this.lname = lname;
        this.Wage = wage;
        this.edlevel = edlevel;
        this.city = city;
        this.i = i;

    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEdlevel() {
        return edlevel;
    }

    public void setEdlevel(String edlevel) {
        this.edlevel = edlevel;
    }

    public int getWage() {
        return Wage;
    }

    public void setWage(int wage) {
        Wage = wage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }
}
