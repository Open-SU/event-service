-- Insert test data for the Event entity

-- Event 1
INSERT INTO Event (id, name, description, price, location, start_date, end_date, organizer_id, creator_id, created_at,
                   updated_at)
VALUES ('60c0a5a3-6cf6-4853-bec9-501b3ced8bd6',
        'Test Event 1',
        'This is a test event description 1',
        25.0,
        'Test Location 1',
        '2023-11-10 12:00:00',
        '2023-11-11 18:00:00',
        'e81d4bd8-ae61-4d26-9da1-4fe472f214ec',
        'f47ac10b-58cc-4372-a567-0e02b2c3d479',
        '2023-11-10 10:00:00',
        '2023-11-10 10:00:00');

-- Event 2
INSERT INTO Event (id, name, description, price, location, start_date, end_date, organizer_id, creator_id, created_at,
                   updated_at)
VALUES ('cacf454c-151b-45e6-ac78-18dd825c72e3',
        'Test Event 2',
        'This is a test event description 2',
        30.0,
        'Test Location 2',
        '2023-11-12 14:00:00',
        '2023-11-13 20:00:00',
        'a6e5e4ad-07a1-4d3d-a25c-1f47a7e1d9cc',
        'c7a5a9f7-76fa-4f35-8c7b-6939c40d69eb',
        '2023-11-11 09:00:00',
        '2023-11-11 09:00:00');

-- Event 3
INSERT INTO Event (id, name, description, price, location, start_date, end_date, organizer_id, creator_id, created_at,
                   updated_at)
VALUES ('69e9357f-edd2-4c97-80b6-9ba79566b237',
        'Conference XYZ',
        'Annual tech conference',
        50.0,
        'Conference Center A',
        '2023-11-20 09:00:00',
        '2023-11-22 18:00:00',
        'd60b8bda-5e20-4c1f-b67d-1f4eac377677',
        'f47ac10b-58cc-4372-a567-0e02b2c3d479',
        '2023-11-15 15:30:00',
        '2023-11-15 15:30:00');

-- Event 4
INSERT INTO Event (id, name, description, price, location, start_date, end_date, organizer_id, creator_id, created_at,
                   updated_at)
VALUES ('598b964e-8020-426c-b302-c1bcff8eb72f',
        'Music Festival',
        'Annual music festival featuring various artists',
        75.0,
        'City Park',
        '2023-12-01 16:00:00',
        '2023-12-03 22:00:00',
        'a6e5e4ad-07a1-4d3d-a25c-1f47a7e1d9cc',
        'c7a5a9f7-76fa-4f35-8c7b-6939c40d69eb',
        '2023-11-25 11:45:00',
        '2023-11-25 11:45:00');

-- Event 5
INSERT INTO Event (id, name, description, price, location, start_date, end_date, organizer_id, creator_id, created_at,
                   updated_at)
VALUES ('bf0e0032-dae4-4d62-a2ac-a8f1751cf409',
        'Community Cleanup',
        'Volunteer event to clean up the neighborhood',
        0.0,
        'Various locations',
        '2023-11-30 10:00:00',
        '2023-11-30 14:00:00',
        'e81d4bd8-ae61-4d26-9da1-4fe472f214ec',
        'f47ac10b-58cc-4372-a567-0e02b2c3d479',
        '2023-11-28 08:00:00',
        '2023-11-28 08:00:00');