package bhuman.message.messages;

import bhuman.message.Message;
import bhuman.message.data.ArrayReader;
import bhuman.message.data.Eigen;
import bhuman.message.data.EnumReader;
import bhuman.message.data.NativeReaders;
import bhuman.message.data.StreamReader;
import java.nio.ByteBuffer;
import java.util.List;

/**
 *
 * @author Felix Thielke
 */
public class GoalPercept extends Message<GoalPercept> {

    public static class GoalPost implements StreamReader<GoalPost> {

        public static enum Position {

            IS_UNKNOWN, IS_LEFT, IS_RIGHT
        }

        public Position position;
        public Eigen.Vector2i positionInImage;
        public Eigen.Vector2f positionOnField;

        @Override
        public GoalPost read(ByteBuffer stream) {
            position = new EnumReader<Position>(Position.class).read(stream);
            positionInImage = new Eigen.Vector2i().read(stream);
            positionOnField = new Eigen.Vector2f().read(stream);

            return this;
        }

    }

    public List<GoalPost> goalPosts;
    public long timeWhenGoalPostLastSeen;
    public long timeWhenCompleteGoalLastSeen;

    @Override
    public GoalPercept read(ByteBuffer stream) {
        goalPosts = new ArrayReader<GoalPost>(GoalPost.class).read(stream);
        timeWhenGoalPostLastSeen = NativeReaders.uintReader.read(stream);
        timeWhenCompleteGoalLastSeen = NativeReaders.uintReader.read(stream);

        return this;
    }

}