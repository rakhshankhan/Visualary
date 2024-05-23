package com.rakhshankhan.visualary.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class CohereResponse {

    private List<Reply> replies;

    @Getter
    @Setter
    public static class Reply {

        private String text;

    }

}
