INSERT INTO books (id, title, author, isbn, price)
VALUES (1, 'Book 1', 'Author 1', '13245768', 350.05);

INSERT INTO categories (id, name)
VALUES (1, 'Fiction');

INSERT INTO books_categories (book_id, category_id)
VALUES (1, 1);

INSERT INTO shopping_carts (id, user_id)
VALUES (1, 1);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (1, 1, 1, 5);
