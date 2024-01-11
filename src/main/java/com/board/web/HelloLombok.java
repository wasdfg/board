package com.board.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelloLombok {
    private String hello;
    private int lombok;

    public int getLombok() { //getter 생성
        return lombok;
    }

    public String getHello() { //getter 생성
        return hello;
    }

    public void setHello(String hello) { //setter 생성
        this.hello = hello;
    }

    public void setLombok(int lombok) { //setter 생성
        this.lombok = lombok;
    }
    public static void main(String[] args){
        HelloLombok helloLombok = new HelloLombok();
        helloLombok.setHello("헬로"); //부름받으면 나오는 함수
        helloLombok.setLombok(5); //부름받으면 나오는 함수

        helloLombok.getHello(); //부르는 함수
        helloLombok.getLombok(); //부르는 함수
    }
}