package com.board.web;

import lombok.Getter;
import lombok.Setter; //RequiredArgsConstructor가 선언되었으므로 속성값 변경이 불가능 하기에 setter도 사용 불가
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor //final을 사용하기 위한 어노테이션 
public class HelloLombok {
    private final String hello; //final로 생성되면 더이상 속성값이 변경 불가능
    private final int lombok;

  /*  public int getLombok() { //getter 생성
        return lombok;
    }

    public String getHello() { //getter 생성
        return hello;
    }*/

    /*public void setHello(String hello) { //setter 생성
        this.hello = hello;
    }

    public void setLombok(int lombok) { //setter 생성
        this.lombok = lombok;
    }*/

    /*public HelloLombok(String hello,int lombok){
        this.hello = hello;
        this.lombok = lombok;
    }*/
    public static void main(String[] args){
        /*HelloLombok helloLombok = new HelloLombok();
        helloLombok.setHello("헬로"); //부름받으면 나오는 함수
        helloLombok.setLombok(5); //부름받으면 나오는 함수

        helloLombok.getHello(); //부르는 함수
        helloLombok.getLombok(); //부르는 함수*/
        HelloLombok helloLombok = new HelloLombok("헬로",5);
        System.out.println(helloLombok.getHello());
        System.out.println(helloLombok.getLombok());
    }
}