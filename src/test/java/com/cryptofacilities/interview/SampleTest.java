package com.cryptofacilities.interview;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by CF-8 on 6/27/2017.
 */
public class SampleTest {


    @Test
    public void testAddOrder() {
        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        // create orders
        Order buy = new Order("order1", "VOD.L", Side.buy, 200, 10);
        // send order
        orderBookManager.addOrder(buy);
        long expOrderNumber = 1;
        long orderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 200);
        assertEquals("There is 1 order in the OBM", expOrderNumber, orderNumber);
    }

    @Test
    public void testModifyOrder() {
        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        // create orders
        Order buy = new Order("order1", "VOD.L", Side.buy, 55, 10);
        Order buy2 = new Order("order2", "VOD.L", Side.buy, 66, 10);
        Order buy21 = new Order("order21", "VOD.L", Side.buy, 66, 10);
        Order buy22 = new Order("order22", "VOD.L", Side.buy, 66, 10);
        Order buy23 = new Order("order23", "VOD.L", Side.buy, 66, 10);
        Order buy3 = new Order("order3", "VOD.L", Side.buy, 77, 10);
        Order buy4 = new Order("order4", "VOD.L", Side.buy, 88, 10);

        // send order
        orderBookManager.addOrder(buy);
        orderBookManager.addOrder(buy2);
        orderBookManager.addOrder(buy21);
        orderBookManager.addOrder(buy22);
        orderBookManager.addOrder(buy23);
        orderBookManager.addOrder(buy3);
        orderBookManager.addOrder(buy4);

        long expOrderNumber = 4;
        long orderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 66);
        assertEquals("There are 4 orders in the OBM before modifying", expOrderNumber, orderNumber);
        //

        long newQty = 2;
        orderBookManager.modifyOrder("order22", newQty);

        List<Order> orderList = orderBookManager.getOrdersAtLevel("VOD.L", Side.buy, 66);
        assertEquals("order22 still has pos 2 in level after qty decrease.", orderList.get(2).getOrderId(), "order22");

        long totalNewQty=32;
        long qty = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.buy, 66);
        assertEquals(String.format("Price for order in the OBM has not been modified to %s", qty), totalNewQty, qty);

        newQty = 47;
        orderBookManager.modifyOrder("order21", newQty);
        //Update orderList
        orderList = orderBookManager.getOrdersAtLevel("VOD.L", Side.buy, 66);
        assertEquals("order21 goes to end of queue of orders in its level after qty increase.", "order21", orderList.get(orderList.size()-1).getOrderId());

        //
        totalNewQty = 69;
        qty = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.buy, 66);
        assertEquals(String.format("Price for order in the OBM has not been modified to %s", qty), totalNewQty, qty );


    }

    @Test
    public void testDeleteOrder() {
        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        // create orders
        Order buy = new Order("order1", "VOD.L", Side.buy, 200, 10);
        // send order
        orderBookManager.addOrder(buy);

        long expOrderNumber = 1;
        long orderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 200);
        assertEquals("There is 1 order before removing", expOrderNumber, orderNumber);
        //
        orderBookManager.deleteOrder("order1");

        expOrderNumber = 0;
        orderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 200);
        assertEquals("There are no orders", expOrderNumber, orderNumber);
    }

    /**
     * The An orderbook is created and some orders added, when asking for the best price, the highest bid is expected.
     */
    @Test
    public void testBestBidPrice() {
        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // create order
        Order buy = new Order("order1", "VOD.L", Side.buy, 200, 10);
        Order buy2 = new Order("order2", "VOD.L", Side.buy, 300, 10);
        Order buy3 = new Order("order3", "VOD.L", Side.buy, 400, 10);
        Order buy4 = new Order("order4", "VOD.L", Side.buy, 500, 10);
        Order buy5 = new Order("order5", "VOD.L", Side.buy, 600, 10);

        // send order
        orderBookManager.addOrder(buy);
        orderBookManager.addOrder(buy2);
        orderBookManager.addOrder(buy3);
        orderBookManager.addOrder(buy4);
        orderBookManager.addOrder(buy5);
        //

        // check that best price is 600
        long expectedPrice = 600;
        long actualPrice = orderBookManager.getBestPrice("VOD.L", Side.buy);
        assertEquals("Best bid price is 600", expectedPrice, actualPrice);
    }

    /**
     * The An orderbook is created and some orders added, when asking for the best price, the lowest ask is expected.
     */
    @Test
    public void testBestAskPrice() {
        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // create order
        Order sell = new Order("order1", "VOD.L", Side.sell, 200, 10);
        Order sell2 = new Order("order2", "VOD.L", Side.sell, 300, 10);
        Order sell3 = new Order("order3", "VOD.L", Side.sell, 400, 10);
        Order sell4 = new Order("order4", "VOD.L", Side.sell, 500, 10);
        Order sell5 = new Order("order5", "VOD.L", Side.sell, 600, 10);

        // send order
        orderBookManager.addOrder(sell);
        orderBookManager.addOrder(sell2);
        orderBookManager.addOrder(sell3);
        orderBookManager.addOrder(sell4);
        orderBookManager.addOrder(sell5);


        // check that best price is 200
        long expectedPrice = 200;
        long actualPrice = orderBookManager.getBestPrice("VOD.L", Side.sell);
        assertEquals("Best bid price is 600", expectedPrice, actualPrice);
    }

    @Test
    public void testNumberOrOrders() {


        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // create orders
        Order buy = new Order("order1", "VOD.L", Side.buy, 200, 10);
        Order buy2 = new Order("order2", "VOD.L", Side.buy, 200, 10);
        Order buy3 = new Order("order3", "VOD.L", Side.buy, 100, 10);
        Order buy4 = new Order("order4", "VOD.L", Side.buy, 75, 10);
        Order buy5 = new Order("order5", "VOD.L", Side.buy, 66, 10);

        //
        Order sell = new Order("order1", "VOD.L", Side.sell, 250, 10);
        Order sell2 = new Order("order2", "VOD.L", Side.sell, 300, 10);
        Order sell3 = new Order("order3", "VOD.L", Side.sell, 300, 10);
        Order sell4 = new Order("order4", "VOD.L", Side.sell, 400, 10);
        Order sell5 = new Order("order5", "VOD.L", Side.sell, 425, 10);


        // send orders
        orderBookManager.addOrder(buy);
        orderBookManager.addOrder(buy2);
        orderBookManager.addOrder(buy3);
        orderBookManager.addOrder(buy4);
        orderBookManager.addOrder(buy5);
        //
        orderBookManager.addOrder(sell);
        orderBookManager.addOrder(sell2);
        orderBookManager.addOrder(sell3);
        orderBookManager.addOrder(sell4);
        orderBookManager.addOrder(sell5);


        // check different order numbers
        long expOrderNumber = 2;
        long actualOrderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 200);
        assertEquals("There are 2 order numbers for 200", expOrderNumber, actualOrderNumber);
        expOrderNumber = 1;
        actualOrderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 100);
        assertEquals("There are 3 order numbers of 100", expOrderNumber, actualOrderNumber);
        // Buy at 400 no orders
        expOrderNumber = 0;
        actualOrderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.buy, 400);
        assertEquals("There are no order numbers for 400", expOrderNumber, actualOrderNumber);

        //Sell, no orders at 100
        expOrderNumber = 0;
        actualOrderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.sell, 100);
        assertEquals("There are no order numbers for sell", expOrderNumber, actualOrderNumber);

        expOrderNumber = 1;
        actualOrderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.sell, 250);
        assertEquals("There are no order numbers for sell", expOrderNumber, actualOrderNumber);


        expOrderNumber = 2;
        actualOrderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.sell, 300);
        assertEquals("There are no order numbers for sell", expOrderNumber, actualOrderNumber);

        expOrderNumber = 1;
        actualOrderNumber = orderBookManager.getOrderNumAtLevel("VOD.L", Side.sell, 400);
        assertEquals("There are no order numbers for sell", expOrderNumber, actualOrderNumber);


    }

    @Test
    public void testTotalTradeableQty() {
        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // create orders
        Order buy = new Order("order1", "VOD.L", Side.buy, 200, 10);
        Order buy2 = new Order("order2", "VOD.L", Side.buy, 100, 10);
        Order buy3 = new Order("order3", "VOD.L", Side.buy, 50, 10);
        Order buy4 = new Order("order4", "VOD.L", Side.buy, 25, 10);
        //
        Order sell = new Order("order1", "VOD.L", Side.sell, 250, 10);
        Order sell2 = new Order("order2", "VOD.L", Side.sell, 300, 10);
        Order sell3 = new Order("order3", "VOD.L", Side.sell, 300, 10);
        Order sell4 = new Order("order4", "VOD.L", Side.sell, 400, 10);
        Order sell5 = new Order("order5", "VOD.L", Side.sell, 425, 10);

        // send orders
        orderBookManager.addOrder(buy);
        orderBookManager.addOrder(buy2);
        orderBookManager.addOrder(buy3);
        orderBookManager.addOrder(buy4);
        //
        orderBookManager.addOrder(sell);
        orderBookManager.addOrder(sell2);
        orderBookManager.addOrder(sell3);
        orderBookManager.addOrder(sell4);
        orderBookManager.addOrder(sell5);

        long expectedQty = -1;
        long actualQty = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.buy, 9999);
        assertEquals("Tradeable qty is 0 for this level", expectedQty, actualQty);

        expectedQty = -1;
        actualQty = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.buy, 300);
        assertEquals("Tradeable qty is 0 for this level", expectedQty, actualQty);

        expectedQty = 20;
        actualQty = orderBookManager.getTotalQuantityAtLevel("VOD.L", Side.sell, 300);
        assertEquals("Tradeable qty is 20 for this level", expectedQty, actualQty);

    }


    @Test
    public void testTotalTradeableVol() {
        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // create orders
        Order buy = new Order("order1", "VOD.L", Side.buy, 200, 10);
        Order buy2 = new Order("order2", "VOD.L", Side.buy, 100, 10);
        Order buy3 = new Order("order3", "VOD.L", Side.buy, 50, 10);
        Order buy4 = new Order("order4", "VOD.L", Side.buy, 25, 10);
        //
        Order sell = new Order("order1", "VOD.L", Side.sell, 250, 10);
        Order sell2 = new Order("order2", "VOD.L", Side.sell, 300, 10);
        Order sell3 = new Order("order3", "VOD.L", Side.sell, 300, 10);
        Order sell4 = new Order("order4", "VOD.L", Side.sell, 400, 10);
        Order sell5 = new Order("order5", "VOD.L", Side.sell, 425, 10);

        // send orders
        orderBookManager.addOrder(buy);
        orderBookManager.addOrder(buy2);
        orderBookManager.addOrder(buy3);
        orderBookManager.addOrder(buy4);
        //
        orderBookManager.addOrder(sell);
        orderBookManager.addOrder(sell2);
        orderBookManager.addOrder(sell3);
        orderBookManager.addOrder(sell4);
        orderBookManager.addOrder(sell5);

        long expectedVol = -1;
        long actualQty = orderBookManager.getTotalVolumeAtLevel("VOD.L", Side.buy, 9999);
        assertEquals("Tradeable vol is 0 for this level", expectedVol, actualQty);

        expectedVol = -1;
        actualQty = orderBookManager.getTotalVolumeAtLevel("VOD.L", Side.buy, 300);
        assertEquals("Tradeable vol is 0 for this level", expectedVol, actualQty);

        expectedVol = 6000;
        actualQty = orderBookManager.getTotalVolumeAtLevel("VOD.L", Side.sell, 300);
        assertEquals("Tradeable vol is 0 for this level", expectedVol, actualQty);

    }


    @Test
    public void testBuySellWrongOrderID() {
        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        // create orders
        Order buy = new Order("order1", "VOD.L", Side.sell, 200, 10);
        Order buy2 = new Order("order2", "VOD.L", Side.sell, 100, 10);
        Order buy3 = new Order("order3", "VOD.L", Side.sell, 50, 10);
        Order buy4 = new Order("order4", "VOD.L", Side.sell, 25, 10);

        // send order
        orderBookManager.addOrder(buy);
        orderBookManager.addOrder(buy2);
        orderBookManager.addOrder(buy3);
        orderBookManager.addOrder(buy4);

        long expectedBestPrice = -1;
        long actualBestPrice = orderBookManager.getBestPrice("NOPE.L", Side.sell);
        assertEquals("No orders with this instrument ID", expectedBestPrice, actualBestPrice);

        actualBestPrice = orderBookManager.getBestPrice("NOPE.L", Side.buy);
        assertEquals("No orders with this instrument ID", expectedBestPrice, actualBestPrice);

    }


    @Test
    public void testListOrdersOnLevelsAndSides() {
        // create order book
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // create orders
        Order buy = new Order("order1", "VOD.L", Side.buy, 200, 10);
        Order buy2 = new Order("order2", "VOD.L", Side.buy, 100, 10);
        Order buy3 = new Order("order3", "VOD.L", Side.buy, 50, 10);
        Order buy4 = new Order("order4", "VOD.L", Side.buy, 25, 10);
        //
        Order sell = new Order("order1", "VOD.L", Side.sell, 250, 10);
        Order sell2 = new Order("order2", "VOD.L", Side.sell, 300, 10);
        Order sell3 = new Order("order3", "VOD.L", Side.sell, 300, 10);
        Order sell31 = new Order("order31", "VOD.L", Side.sell, 300, 10);
        Order sell32 = new Order("order32", "VOD.L", Side.sell, 300, 10);
        Order sell33 = new Order("order33", "VOD.L", Side.sell, 300, 10);
        Order sell34 = new Order("order34", "VOD.L", Side.sell, 300, 10);
        //
        Order sell4 = new Order("order4", "VOD.L", Side.sell, 400, 10);
        Order sell5 = new Order("order5", "VOD.L", Side.sell, 425, 10);

        // send orders
        orderBookManager.addOrder(buy);
        orderBookManager.addOrder(buy2);
        orderBookManager.addOrder(buy3);
        orderBookManager.addOrder(buy4);
        //
        orderBookManager.addOrder(sell);
        orderBookManager.addOrder(sell2);
        orderBookManager.addOrder(sell3);
        orderBookManager.addOrder(sell31);
        orderBookManager.addOrder(sell32);
        orderBookManager.addOrder(sell33);
        orderBookManager.addOrder(sell34);

        orderBookManager.addOrder(sell4);
        orderBookManager.addOrder(sell5);

        long expectedVol = -1;
        long actualQty = orderBookManager.getTotalVolumeAtLevel("VOD.L", Side.buy, 9999);
        assertEquals("Tradeable vol is 0 for this level", expectedVol, actualQty);

        expectedVol = -1;
        actualQty = orderBookManager.getTotalVolumeAtLevel("VOD.L", Side.buy, 300);
        assertEquals("Tradeable vol is 0 for this level", expectedVol, actualQty);

        expectedVol = 18000;
        actualQty = orderBookManager.getTotalVolumeAtLevel("VOD.L", Side.sell, 300);
        assertEquals("Tradeable vol is 18000 for this level", expectedVol, actualQty);

    }
}