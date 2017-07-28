package com.cryptofacilities.interview;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by CF-8 on 6/27/2017.
 */
public class OrderBookManagerImpl implements OrderBookManager {


    //Comparator to keep orders sorted as they arrive.
    Comparator<Order> comparatorOrders = (x, y) ->
    {
        if (x.getCreateTime() == y.getCreateTime()) return 0;
        else return x.getCreateTime() > y.getCreateTime() ? 1 : -1;
    };

    //Comparator to sort levels on highest price fist for bid and lowest first for ask.
    Comparator<OrderKey> comparatorGroupLevels = (x, y) ->
    {
        //Equal
        if (x.getSide().equals(y.getSide()) && x.getPrice().equals(y.getPrice())) return 0;

            //Each side has different sorting criteria
        else if (x.getSide().equals(Side.buy)) {
            return x.getPrice() > y.getPrice() ? -1 : 1;
        } else //Sell
        {
            return x.getPrice() > y.getPrice() ? 1 : -1;
        }
    };


    //Group by level - side and keep them sorted
    Map<OrderKey, Set<Order>> orders = new TreeMap(comparatorGroupLevels);
    //Corr. order - level
    Map<String, OrderKey> levels = new HashMap<>();


    public void addOrder(Order order) {
        Long price = order.getPrice();
        OrderKey key = new OrderKey(price, order.getSide());
        Set<Order> level = orders.get(key);

        if (level == null || level.size() == 0) //Could also be empty
        {
            level = new TreeSet(comparatorOrders);
        }
        //
        level.add(order);
        orders.put(key, level);

        if (levels.get(order.getOrderId()) == null || level.size() == 0) {
            levels.put(order.getOrderId(), new OrderKey(order.getPrice(), order.getSide()));
        }
    }

    public void modifyOrder(String orderId, long newQuantity) {
        try {
            Set<Order> ordersByLevel = orders.get(levels.get(orderId));
            Order order = ordersByLevel.stream()
                    .filter(x -> x.getOrderId().equals(orderId))
                    .findFirst().orElseThrow(() -> new Exception("Order not found"));


            //If quantity increased, order goes at end
            if (order.getQuantity() < newQuantity) {
                order.setQuantity(newQuantity);
                //Remove and add so we make sure the object is sorted.
                ordersByLevel.remove(order);
                ordersByLevel.add(order.reset()); //Resort order
            } else {
                //If quantity decreased, order maintains pos.
                order.setQuantity(newQuantity);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    public void deleteOrder(String orderId) {

        Set<Order> ordersByLevel = orders.get(levels.get(orderId));

        Order order = ordersByLevel.stream()
                .filter(x -> x.getOrderId().equals(orderId)).findAny().orElse(null);

        ordersByLevel.remove(order);
        levels.remove(orderId);
    }


    public long getBestPrice(String instrument, Side side) {
        if (null == instrument || instrument == "") {
            return -1;
        }
        switch (side) {
            case buy:
                List<Order> buyOrderList = new ArrayList<Order>();
                final List<Order> resultBuyList;

                orders.values().stream().forEach(z -> buyOrderList.addAll(z));
                resultBuyList = buyOrderList.stream()
                        .filter(x -> x.getInstrument().equals(instrument))
                        .filter(x -> x.getSide().equals(Side.buy))
                        //Highest price
                        .sorted(Comparator.comparing(x -> x.getPrice() * -1))
                        .limit(1)
                        .collect(Collectors.toList());
                return resultBuyList.size() != 0 ? resultBuyList.get(0).getPrice() : -1;
            //
            case sell:
                List<Order> sellOrderList = new ArrayList<Order>();
                final List<Order> resultSellList;

                orders.values().stream().forEach(z -> sellOrderList.addAll(z));
                resultSellList = sellOrderList.stream()
                        .filter(x -> x.getInstrument().equals(instrument))
                        .filter(x -> x.getSide().equals(Side.sell))
                        //Lowest price
                        .sorted(Comparator.comparing(x -> x.getPrice()))
                        .limit(1).collect(Collectors.toList());
                return resultSellList.size() != 0 ? resultSellList.get(0).getPrice() : -1;
        }
        return -1;
    }

    public long getOrderNumAtLevel(String instrument, Side side, long price) {

        Set<Order> ordersAtLevel = orders.get(new OrderKey(price, side));

        if (ordersAtLevel != null) {
            return ordersAtLevel.size();
        }
        return 0L;
    }

    public long getTotalQuantityAtLevel(String instrument, Side side, long price) {

        Set<Order> resOrders = orders.get(new OrderKey(price, side));

        return resOrders == null ? -1 : resOrders.stream()
                .filter(x -> x.getSide().equals(side))
                .filter(x -> x.getInstrument().equals(instrument))
                .mapToLong(a -> a.getQuantity()).sum();
    }

    public long getTotalVolumeAtLevel(String instrument, Side side, long price) {
        Set<Order> resOrders = orders.get(new OrderKey(price, side));

        return resOrders == null ? -1 : resOrders.stream()
                .filter(x -> x.getSide().equals(side))
                .filter(x -> x.getInstrument().equals(instrument))
                .mapToLong(a -> a.getQuantity() * a.getPrice()).sum();
    }

    public List<Order> getOrdersAtLevel(String instrument, Side side, long price) {
        return orders.get(new OrderKey(price, side)).stream()
                .filter(x -> x.getSide().equals(side))
                .filter(x -> x.getInstrument().equals(instrument))
                .collect(Collectors.toList());
    }

    /**
     * Class to help sort the orders
     */
    class OrderKey {

        Long price;
        Side side;
        public OrderKey(Long price, Side side) {
            this.price = price;
            this.side = side;
        }

        public Long getPrice() {
            return price;
        }

        public Side getSide() {
            return side;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OrderKey orderKey = (OrderKey) o;

            if (price != null ? !price.equals(orderKey.price) : orderKey.price != null) return false;
            return side == orderKey.side;
        }

        @Override
        public int hashCode() {
            int result = price != null ? price.hashCode() : 0;
            result = 31 * result + (side != null ? side.hashCode() : 0);
            return result;
        }
    }
}