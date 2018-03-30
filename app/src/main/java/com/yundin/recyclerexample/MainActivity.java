package com.yundin.recyclerexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yundin.recyclerexample.api.ApiService;
import com.yundin.recyclerexample.api.GetAllGoodsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GoodsAdapter adapter;
    private Button addButton;
    private TextView sumTV;
    private int sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        updateData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == resultCode) {
            int id = data.getIntExtra(AddProductActivity.ID, -1);
            String title = data.getStringExtra(AddProductActivity.TITLE);
            int price = data.getIntExtra(AddProductActivity.PRICE, 0);
            String image = data.getStringExtra(AddProductActivity.IMAGE);
            adapter.appendItemWithNotify(new Product(id, image, price, title));
        }
    }

    private void init() {
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setNestedScrollingEnabled(false);
        addButton = findViewById(R.id.add_button);
        sumTV = findViewById(R.id.sum_text);
        sumTV.setText(getString(R.string.sum, sum));

        View.OnClickListener sumListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sum += (int) v.getTag();
                sumTV.setText(getString(R.string.sum, sum));
            }
        };
        adapter = new GoodsAdapter(getApplicationContext(), sumListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void updateData() {
        ApiService.getApiInterface()
                .getAllGoods()
                .enqueue(new Callback<GetAllGoodsResponse>() {
                    @Override
                    public void onResponse(Call<GetAllGoodsResponse> call, Response<GetAllGoodsResponse> response) {
                        if (response.body() != null) {
                            adapter.updateDataWithNotify(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<GetAllGoodsResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Нет интернет соединения", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
