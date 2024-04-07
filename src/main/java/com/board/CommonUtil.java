package com.board;

import org.springframework.stereotype.Component;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@Component //commonutil을 bean으로 사용하기 위해 사용
public class CommonUtil {
    public String markdown(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown); //마크다운으로 작성된 텍스트를 html로 변환시킨다.
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
