import React from "react";
import { useRouter } from "next/router";
import { trigger } from "swr";
import useSWR from "swr";
import CommentAPI from "../../lib/api/comment";
import { SERVER_BASE_URL } from "../../lib/utils/constant";
import storage from "../../lib/utils/storage";
import checkLogin from "../../lib/utils/checkLogin";

interface ReactionButtonsProps {
  comment: {
    id: string;
    likesCount: number;
    dislikesCount: number;
    currentUserReaction: "LIKE" | "DISLIKE" | null;
  };
}

const ReactionButtons: React.FC<ReactionButtonsProps> = ({ comment }) => {
  const { data: currentUser } = useSWR("user", storage);
  const isLoggedIn = checkLogin(currentUser);
  const router = useRouter();
  const {
    query: { pid },
  } = router;

  const [isLoading, setLoading] = React.useState(false);

  const handleLike = async () => {
    if (!isLoggedIn) return;
    setLoading(true);

    if (comment.currentUserReaction === "LIKE") {
      await CommentAPI.unlike(pid as string, comment.id);
    } else {
      await CommentAPI.like(pid as string, comment.id);
    }

    trigger(`${SERVER_BASE_URL}/articles/${pid}/comments`);
    setLoading(false);
  };

  const handleDislike = async () => {
    if (!isLoggedIn) return;
    setLoading(true);

    if (comment.currentUserReaction === "DISLIKE") {
      await CommentAPI.undislike(pid as string, comment.id);
    } else {
      await CommentAPI.dislike(pid as string, comment.id);
    }

    trigger(`${SERVER_BASE_URL}/articles/${pid}/comments`);
    setLoading(false);
  };

  return (
    <span className="comment-reactions">
      <button
        className={`btn btn-sm ${
          comment.currentUserReaction === "LIKE"
            ? "btn-primary"
            : "btn-outline-primary"
        }`}
        onClick={handleLike}
        disabled={isLoading || !isLoggedIn}
      >
        <i className="ion-thumbsup" /> {comment.likesCount}
      </button>
      &nbsp;
      <button
        className={`btn btn-sm ${
          comment.currentUserReaction === "DISLIKE"
            ? "btn-danger"
            : "btn-outline-danger"
        }`}
        onClick={handleDislike}
        disabled={isLoading || !isLoggedIn}
      >
        <i className="ion-thumbsdown" /> {comment.dislikesCount}
      </button>
    </span>
  );
};

export default ReactionButtons;
