package @package@;

public class Version {
    public static final String PRODUCT = "@product@";
    public static final String VERSION = "@version@";

    public static final String version(){
        return PRODUCT+" version "+VERSION;
    }

    public static void main(String[] args){
        System.out.println(version());
    }
}