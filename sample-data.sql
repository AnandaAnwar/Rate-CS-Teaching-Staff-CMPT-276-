-- Sample data for local testing. All names and emails are made up.
-- Run this ONCE (running it twice inserts everything twice).
INSERT INTO staff_ratings
    (name, email, role_type, clarity, niceness, knowledgeable_score, comment_text, created_at, updated_at)
VALUES
    ('Maya Okafor',      'maya.okafor@example.com',      'PROF',       9,  8, 10, 'Explains hard ideas step by step and always leaves time for questions.',            now() - interval '10 days', now() - interval '10 days'),
    ('Daniel Reyes',     'daniel.reyes@example.com',     'TA',         8,  9,  7, 'Patient in labs and quick to answer on the forum.',                                  now() - interval '9 days',  now() - interval '9 days'),
    ('Priya Natarajan',  'priya.natarajan@example.com',  'INSTRUCTOR', 7,  8,  9, 'Strong examples, but lectures sometimes run over time.',                             now() - interval '8 days',  now() - interval '8 days'),
    ('Liam Chen',        'liam.chen@example.com',        'TA',         4,  6,  5, 'Feedback on assignments was vague and slow to arrive.',                              now() - interval '7 days',  now() - interval '7 days'),
    ('Sofia Marchetti',  'sofia.marchetti@example.com',  'PROF',       6,  5,  9, 'Clearly knows the material, but the pace is fast and the slides are dense.',         now() - interval '6 days',  now() - interval '6 days'),
    ('Omar Haddad',      'omar.haddad@example.com',      'STAFF',      9, 10,  7, 'Sorted out my course registration problem in one visit.',                            now() - interval '5 days',  now() - interval '5 days'),
    ('Hannah Berg',      'hannah.berg@example.com',      'TA',         3,  4,  5, 'Hard to follow in tutorials. Office hours were more helpful.',                       now() - interval '4 days',  now() - interval '4 days'),
    ('Maya Okafor',      'maya.okafor@example.com',      'PROF',      10,  9, 10, NULL,                                                                                 now() - interval '3 days',  now() - interval '3 days'),
    ('Tobias Lindqvist', 'tobias.lindqvist@example.com', 'INSTRUCTOR', 8,  7,  8, 'Fair exams and clear grading rubrics.',                                              now() - interval '2 days',  now() - interval '2 days'),
    ('Aisha Rahman',     'aisha.rahman@example.com',     'PROF',       5,  7,  8, 'Great research background and very approachable, but the course outline changed twice during the term and that made planning difficult.', now() - interval '1 day', now() - interval '1 day'),
    ('Ethan Walker',     'ethan.walker@example.com',     'INSTRUCTOR', 2,  3,  6, 'Struggled to explain recursion and rarely answered questions in class.',            now(),                      now());