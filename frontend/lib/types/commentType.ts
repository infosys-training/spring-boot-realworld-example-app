export interface Comments {
  comments: CommentType[];
}

export type CommentType = {
  createdAt: number;
  id: string;
  body: string;
  slug: string;
  author: Author;
  updatedAt: number;
  likesCount: number;
  dislikesCount: number;
  currentUserReaction: 'LIKE' | 'DISLIKE' | null;
};

export type Author = {
  username: string;
  bio: string;
  image: string;
  following: boolean;
};
