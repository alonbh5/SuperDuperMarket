package course.java.sdm.engine;

public class Person implements HasName {

    private  final Long m_IDNumber;
    private String m_Name;


    protected Person(Long i_IDNumber,String i_Name) {
        this.m_IDNumber = i_IDNumber;
        this.m_Name= i_Name;
    }

    public Long getId () {return m_IDNumber;}

    @Override
    public String getName() {
        return m_Name;
    }

    @Override
    public void setName(String Input) {
        m_Name=Input;
    }

    long getIDNumber ()
    {
        return m_IDNumber;
    }

}
