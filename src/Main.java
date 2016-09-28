public class Main {

    public static void main(String[] args) throws Exception {

        String url = "";
        String param = "";

        RestUtil restUtil = new RestUtil();
        System.out.println(restUtil.simplePost(url, param));
    }
}
