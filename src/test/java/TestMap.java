import java.util.HashMap;

public class TestMap {
    private static HashMap<String, String> map = new HashMap<String, String>() {
        {
            put("Name", "June");
            put("QQ", "2572073701");
        }
    };

    static {
        System.out.println("Static block called：静态块被调用");
    }

    {
        System.out.println("Instance initializer called：实例初始化块被调用");
    }

    public TestMap() {
        System.out.println("Constructor called：构造器被调用");
    }

    public static void main(String[] args) {
        new TestMap();
        System.out.println("=======================");
        new TestMap();
    }
}
