package com.placehub.boundedContext.post.service;

import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public long createPost(long userId, long placeId, String content, boolean openToPublic) {
        Post post = Post.builder()
                .userId(userId)
                .placeId(placeId)
                .content(content)
                .openToPublic(openToPublic)
                .build();

        return postRepository.save(post).getId();
    }
}
