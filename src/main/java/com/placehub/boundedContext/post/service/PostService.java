package com.placehub.boundedContext.post.service;

import com.placehub.boundedContext.post.entity.Post;
import com.placehub.boundedContext.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public long createPost(long userId, long placeId, String content, boolean openToPublic, LocalDateTime visitedDate) {
        Post post = Post.builder()
                .member(userId)
                .place(placeId)
                .content(content)
                .openToPublic(openToPublic)
                .visitedDate(visitedDate)
                .build();

        return postRepository.save(post).getId();
    }

    public List<Post> getPostsByPlace(long placeId) {
        Optional<List<Post>> postList = postRepository.findPostsByPlace(placeId);

        if (postList.isPresent()) {
            List<Post> posts = postList.get();
            Collections.sort(posts);
            return posts;
        }

        return new ArrayList<>();
    }

    public long changePublicShowing(long id, boolean toChange) throws SQLException {
        Optional<Post> wrappedPost = postRepository.findById(id);

        if (wrappedPost.isPresent()) {
            Post post = wrappedPost.get();
            post = post.toBuilder()
                    .openToPublic(toChange)
                    .build();

            return postRepository.save(post).getId();
        }

        throw new SQLDataException("존재하지 않는 포스트입니다");
    }
}
