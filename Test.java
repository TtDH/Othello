class Test1{
    Test1(){
        System.out.println("Hello from Test1");
        new Test2();
    }

    public static void main(String args[]){
        new Test1();
    }
}


class Test2{
    Test2(){
        System.out.println("Hello from Test2");
    }

    public static void main(String args[]){
        System.out.println("It's Test2 main()");
    }
}


