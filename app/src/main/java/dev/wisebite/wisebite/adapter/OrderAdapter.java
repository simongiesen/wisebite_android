package dev.wisebite.wisebite.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import dev.wisebite.wisebite.R;
import dev.wisebite.wisebite.activity.GetOrderActivity;
import dev.wisebite.wisebite.domain.Order;
import dev.wisebite.wisebite.repository.OrderItemRepository;
import dev.wisebite.wisebite.repository.OrderRepository;
import dev.wisebite.wisebite.service.OrderService;
import dev.wisebite.wisebite.service.ServiceFactory;
import dev.wisebite.wisebite.service.UserService;
import dev.wisebite.wisebite.utils.FirebaseRepository;
import dev.wisebite.wisebite.utils.Preferences;
import dev.wisebite.wisebite.utils.Repository;
import dev.wisebite.wisebite.utils.Utils;

/**
 * Created by albert on 20/03/17.
 * @author albert
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private ArrayList<Order> orders;
    private OrderService orderService;
    private UserService userService;

    public OrderAdapter(ArrayList<Order> ordersList, Context context) {
        this.orders = ordersList;
        this.orderService = ServiceFactory.getOrderService(context);
        this.userService = ServiceFactory.getUserService(context);
        notifyDataSetChanged();
        setListener();
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_item, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderHolder holder, int position) {
        Order current = orders.get(position);
        holder.item = current;
        holder.numberTable.setText(String.valueOf("Table " + current.getTableNumber()));
        holder.price.setText(String.valueOf(calculatePrice(current) + " €"));
        holder.ready.setText(String.valueOf("Ready: " + calculateReady(current) + "%"));
        holder.delivered.setText(String.valueOf("Delivered: " + calculateDelivered(current) + "%"));
        holder.paid.setText(String.valueOf("Paid: " + calculatePaid(current) + "%"));
    }

    private String calculatePrice(Order current) {
        return Utils.toStringWithTwoDecimals(orderService.getPriceOfOrder(current.getId()));
    }

    private String calculateReady(Order current) {
        return Utils.toStringWithTwoDecimals(orderService.getReadyOfOrder(current.getId()));
    }

    private String calculateDelivered(Order current) {
        return Utils.toStringWithTwoDecimals(orderService.getDeliveredOfOrder(current.getId()));
    }

    private String calculatePaid(Order current) {
        return Utils.toStringWithTwoDecimals(orderService.getPaidOfOrder(current.getId()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderHolder extends RecyclerView.ViewHolder {

        public View view;
        Order item;
        TextView numberTable;
        TextView price;
        TextView ready;
        TextView delivered;
        TextView paid;

        OrderHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.numberTable = (TextView) itemView.findViewById(R.id.table_number);
            this.price = (TextView) itemView.findViewById(R.id.price);
            this.ready = (TextView) itemView.findViewById(R.id.ready);
            this.delivered = (TextView) itemView.findViewById(R.id.delivered);
            this.paid = (TextView) itemView.findViewById(R.id.paid);

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), GetOrderActivity.class);
                    intent.putExtra(GetOrderActivity.INTENT_ORDER, item.getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    private void setListener() {
        orderService.setOnChangedListener(new Repository.OnChangedListener() {
            @Override
            public void onChanged(EventType type) {
                if (type.equals(EventType.Full)) {
                    orders = orderService.getActiveOrders(userService.getFirstRestaurantId(Preferences.getCurrentUserEmail()));
                    notifyDataSetChanged();
                }
            }
        });
    }
}
