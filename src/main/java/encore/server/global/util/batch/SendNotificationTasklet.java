package encore.server.global.util.batch;

import encore.server.domain.post.entity.Post;
import encore.server.domain.post.repository.PostRepository;
import encore.server.domain.user.entity.User;
import encore.server.domain.user.repository.UserRepository;
import encore.server.domain.user.service.UserFcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class SendNotificationTasklet implements Tasklet {

    private final UserRepository userRepository;
    private final UserFcmService userFcmService;
    private final PostRepository postRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        List<User> users = userRepository.findAllByDeletedAtIsNull();
        Post popularPost = postRepository.findTopByOrderByLikeCountDesc();

        if (popularPost != null) {
            for (User user : users) {
                userFcmService.notifyUsersByRanking(user, popularPost);
            }
        }
        return RepeatStatus.FINISHED;
    }
}