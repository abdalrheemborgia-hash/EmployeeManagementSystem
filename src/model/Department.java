package model;

/**
 * يمثل قسماً في المؤسسة.
 *
 * @author فريق المشروع
 */
public class Department {
    private int    id;
    private String name;
    private String description;
    private String managerName;

    public Department() {}

    public Department(int id, String name, String description, String managerName) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.managerName = managerName;
    }

    public int    getId()                   { return id; }
    public void   setId(int id)             { this.id = id; }
    public String getName()                 { return name; }
    public void   setName(String n)         { this.name = n; }
    public String getDescription()          { return description; }
    public void   setDescription(String d)  { this.description = d; }
    public String getManagerName()          { return managerName; }
    public void   setManagerName(String m)  { this.managerName = m; }

    @Override
    public String toString() { return name; }
}
