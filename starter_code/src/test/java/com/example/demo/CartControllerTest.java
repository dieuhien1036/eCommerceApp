package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CartControllerTest {

    private CartController cartController;

    /* Mock Repository */
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    /* Value for test */
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static final int ITEM_ID = 1;
    private static final String ITEM_NAME = "item name";
    private static final String ITEM_DESCRIPTION = "item description";
    private static final BigDecimal PRICE = new BigDecimal(10);

    private static final BigDecimal TOTAL = new BigDecimal(20);
    private static final int QUANTITY = 2;


    @Before
    public void init() {
        cartController = new CartController();

        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        Item item = new Item();
        User user = new User();
        Cart cart = new Cart();

        item.setId((long) ITEM_ID);
        item.setName(ITEM_NAME);
        item.setPrice(PRICE);
        item.setDescription(ITEM_DESCRIPTION);

        cart.setItems(null);
        cart.setTotal(TOTAL);
        cart.setUser(user);

        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setCart(cart);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    }

    @Test
    public void add_cart_success() {
        ModifyCartRequest cartRequest = getCartRequest(USERNAME, QUANTITY, ITEM_ID);
        ResponseEntity<Cart> responseResult =  cartController.addTocart(cartRequest);
        Cart cart = responseResult.getBody();

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.OK.value());

        assertNotNull(cart);
        assertEquals(cart.getUser().getUsername(), USERNAME);
        assertEquals(cart.getItems().size(), QUANTITY);
        assertEquals(cart.getTotal(), TOTAL.add(PRICE.multiply(BigDecimal.valueOf(QUANTITY))));
    }

    @Test
    public void add_cart_fail_not_found_user() {
        ModifyCartRequest cartRequest = getCartRequest("username1", QUANTITY, ITEM_ID);
        ResponseEntity<Cart> responseResult =  cartController.addTocart(cartRequest);

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void add_cart_fail_not_found_item() {
        ModifyCartRequest cartRequest = getCartRequest(USERNAME, QUANTITY, ITEM_ID + 1);
        ResponseEntity<Cart> responseResult =  cartController.addTocart(cartRequest);

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void remove_cart_success() {
        ModifyCartRequest cartRequest = getCartRequest(USERNAME, QUANTITY, ITEM_ID);

        ResponseEntity<Cart> responseResult =  cartController.removeFromcart(cartRequest);
        Cart cart = responseResult.getBody();
        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.OK.value());

        assertNotNull(cart);
        assertEquals(cart.getUser().getUsername(), USERNAME);
        assertEquals(cart.getItems().size(), 0);
        assertEquals(cart.getTotal(), new BigDecimal(0));
    }

    public void remove_cart_fail_not_found_user() {
        ModifyCartRequest cartRequest = getCartRequest("username1", QUANTITY, ITEM_ID);
        ResponseEntity<Cart> responseResult =  cartController.removeFromcart(cartRequest);

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void remove_cart_fail_not_found_item() {
        ModifyCartRequest cartRequest = getCartRequest(USERNAME, QUANTITY, ITEM_ID + 1);
        ResponseEntity<Cart> responseResult =  cartController.removeFromcart(cartRequest);

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    private ModifyCartRequest getCartRequest(String username, int quantity, int itemId) {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(itemId);
        cartRequest.setUsername(username);
        cartRequest.setQuantity(quantity);
        return cartRequest;
    }
}
