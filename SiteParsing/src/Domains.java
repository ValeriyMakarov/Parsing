public final class Domains {
    private static String[] domains = {"com", "ru", "ua"};
    public static int length = domains.length;
    public static String get(int index){
        try{
            if (index<0||index>domains.length-1)
                throw new IndexOutOfBoundsException ("Out of bounds!");
            return domains[index];
        }catch (Exception ex){System.out.println(ex);}
        return null;
    }
    public static String[] getDomains(){
        return domains;
    }
}
