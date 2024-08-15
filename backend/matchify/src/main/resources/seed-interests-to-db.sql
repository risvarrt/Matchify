INSERT INTO interest_groups  (id, name) VALUES
(1, 'Sports'),
(2, 'Academics'),
(3, 'Languages'),
(4, 'Food'),
(5, 'Movies'),
(6, 'Music'),
(7, 'Art'),
(8, 'Reading'),
(9, 'Others');

select * from interest_groups ig;

-- Insert queries for InterestCategories table
INSERT INTO interest_categories  (id, category_name, group_id, group_name) VALUES
-- Sports
(101, 'Badminton', 1, 'Sports'),
(102, 'Basketball', 1, 'Sports'),
(103, 'Cricket', 1, 'Sports'),
(104, 'Swimming', 1, 'Sports'),
(105, 'Ice skating', 1, 'Sports'),
(106, 'Soccer', 1, 'Sports'),
(107, 'Tennis', 1, 'Sports'),
(108, 'Gym', 1, 'Sports'),
-- Academics
(201, 'Biology', 2, 'Academics'),
(202, 'Computer Science', 2, 'Academics'),
(203, 'Chemistry', 2, 'Academics'),
(204, 'History', 2, 'Academics'),
(205, 'Literature', 2, 'Academics'),
(206, 'Physics', 2, 'Academics'),
(207, 'Political Science', 2, 'Academics'),
-- Languages
(301, 'English', 3, 'Languages'),
(302, 'French', 3, 'Languages'),
(303, 'Spanish', 3, 'Languages'),
(304, 'German', 3, 'Languages'),
(305, 'Chinese', 3, 'Languages'),
(306, 'Japanese', 3, 'Languages'),
(307, 'Korean', 3, 'Languages'),
(308, 'Italian', 3, 'Languages'),
(309, 'Hindi', 3, 'Languages'),
-- Food
(401, 'Arabic', 4, 'Food'),
(402, 'Indian', 4, 'Food'),
(403, 'Mexican', 4, 'Food'),
(404, 'Chinese', 4, 'Food'),
(405, 'Thai', 4, 'Food'),
(406, 'Italian', 4, 'Food'),
(407, 'Korean', 4, 'Food'),
-- Movies
(501, 'Action', 5, 'Movies'),
(502, 'Drama', 5, 'Movies'),
(503, 'Horror', 5, 'Movies'),
(504, 'Comedy', 5, 'Movies'),
(505, 'Suspense', 5, 'Movies'),
(506, 'Thriller', 5, 'Movies'),
(507, 'Romantic', 5, 'Movies'),
(508, 'Musicals', 5, 'Movies'),
-- Music
(601, 'Classical', 6, 'Music'),
(602, 'Pop', 6, 'Music'),
(603, 'Hip hop', 6, 'Music'),
(604, 'Country', 6, 'Music'),
(605, 'Rock', 6, 'Music'),
(606, 'Jazz', 6, 'Music'),
-- Art
(701, 'Sculpting', 7, 'Art'),
(702, 'Drawing', 7, 'Art'),
(703, 'Painting', 7, 'Art'),
(704, 'Knitting', 7, 'Art'),
(705, 'Visiting Museums', 7, 'Art'),
(706, 'Photography', 7, 'Art'),
-- Reading
(801, 'Book clubs', 8, 'Reading'),
(802, 'literature discussions', 8, 'Reading'),
(803, 'science fiction', 8, 'Reading'),
(804, 'fantasy', 8, 'Reading'),
(805, 'horror', 8, 'Reading'),
(806, 'mystery', 8, 'Reading'),
-- Others
(901, 'Technology', 9, 'Others'),
(902, 'Space', 9, 'Others'),
(903, 'Cars', 9, 'Others'),
(904, 'Modelling', 9, 'Others'),
(905, 'Dancing', 9, 'Others'),
(906, 'Travelling', 9, 'Others'),
(907, 'Cooking', 9, 'Others'),
(908, 'Fishing', 9, 'Others');

select * from interest_categories ic;
