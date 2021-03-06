package com.example.plansplit.Controllers.FragmentControllers.personal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.ExpensesAdapter;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Expense;
import com.example.plansplit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.R.layout.simple_spinner_item;

public class PersonalFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    String[] country;
    Database db;
    private static final String TAG = "PersonalFragment";
    RecyclerView recyclerView;
    ExpensesAdapter adapter;
    int budget;                        //butçe progressBar'da 360 dereceye denk gelecek
    int totExpense;                   //toplam harcamayı gösteriyor.
    ArrayList<Expense> expenseList;
    ProgressBar progressBar;
    TextView progressText, remainingbudget;
    Button addExpense;
    ImageView filter;
    ImageView personstatus;
    ImageView personPhoto;
    TextView monthlySaving;
    EditText expenseName, price;
    String type;
    String selectedFilter;
    Boolean control=false;
    Spinner spin;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personal, container, false);
        db = Database.getInstance();

        monthlySaving = root.findViewById(R.id.monthly_saving);
        monthlySaving.setText(getString(R.string.monthly_saving, 0));

        expenseList = new ArrayList<>();
        db.getBudget(budgetCallBack);

        personPhoto = root.findViewById(R.id.personalOperations_imagePerson);
        personstatus = root.findViewById(R.id.personalOperations_PersonBackGround);
        Picasso.with(getContext()).load(db.getPerson().getImage()).into(personPhoto);


        //country = new String[]{"Yiyecek", "Giyecek", "Kırtasiye", "Temizlik" , "Diğer"};
        country = new String[]{getResources().getStringArray(R.array.expensePersonalItems)[0],
                getResources().getStringArray(R.array.expensePersonalItems)[1],
                getResources().getStringArray(R.array.expensePersonalItems)[2],
                getResources().getStringArray(R.array.expensePersonalItems)[3],
                getResources().getStringArray(R.array.expensePersonalItems)[4]};

        spin = root.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this.getContext(), simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        filter=root.findViewById(R.id.filter);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setProgress(totExpense);
        progressText = root.findViewById(R.id.progress_text);
        addExpense = root.findViewById(R.id.add_expense);
        price = root.findViewById(R.id.price);
        expenseName = root.findViewById(R.id.name);
        remainingbudget = root.findViewById(R.id.remaining_budget);



        recyclerView = root.findViewById(R.id.recycler_expense);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);

        update();

        addExpense.setOnClickListener(this);
        progressText.setOnClickListener(this);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), filter);
                popup.inflate(R.menu.filter_menu_personal);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()){

                            case R.id.filter_food:
                                //selectedFilter=getResources().getString(R.string.title_food);
                                selectedFilter="yiyecek";
                                break;
                            case R.id.filter_wear:
                                //selectedFilter=getResources().getString(R.string.title_wear);
                                selectedFilter="giyecek";
                                break;
                            case R.id.filter_stationery:
                                //selectedFilter=getResources().getString(R.string.title_stationery);
                                selectedFilter="kırtasiye";
                                break;
                            case R.id.filter_hygiene:
                                //selectedFilter=getResources().getString(R.string.title_hygiene);
                                selectedFilter="temizlik";
                                break;
                            case R.id.filter_others:
                                //selectedFilter=getResources().getString(R.string.title_others);
                                selectedFilter="diğer";
                                break;
                            case R.id.filter_all:
                                selectedFilter=getResources().getString(R.string.title_all);
                                break;

                        }
                        filterList(selectedFilter);
                        return false;
                    }
                });
                popup.show();


               //
            }
        });

        return root;
    }

    final Database.BudgetCallBack budgetCallBack = new Database.BudgetCallBack() {
        @Override
        public void onBudgetRetrieveSuccess(int budget) {
            Log.d(TAG, String.valueOf(budget));
            db.getExpenses(expenseCallBack);
            db.getLastMonthSaving(new Database.BudgetCallBack() {
                @Override
                public void onBudgetRetrieveSuccess(int saving) {
                    monthlySaving.setText(getString(R.string.monthly_saving, saving));
                }

                @Override
                public void onError(String error_tag, String error){Log.e(error_tag, error);}
            });
            checkBudget(budget);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
            db.getExpenses(expenseCallBack);
            checkBudget(null);
        }
    };

    final Database.ExpenseCallBack expenseCallBack = new Database.ExpenseCallBack() {
        @Override
        public void onExpenseRetrieveSuccess(ArrayList<Expense> expenses) {
            expenseList.clear();
            expenseList.addAll(expenses);
            totExpense = 0;
            for (Expense expense: expenseList){
                totExpense += Integer.parseInt(expense.getPrice());
            }
            update();
            checkOverBudget();
            checkDate();
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
        }
    };

    //check date of first expense element in order to find if we pass to next month
    //if so delete all Notifications and create a monthly_expense notifications
    private void checkDate(){
        db.getLastLogin(new Database.LoginDateCallBack() {
            @Override
            public void onLoginDateRetrieveSuccess(long date) {
                Calendar c = Calendar.getInstance();
                if (date > c.getTimeInMillis()){
                    return;
                }
                int current_month = c.get(Calendar.MONTH);
                c.setTimeInMillis(date);
                int last_login_month = c.get(Calendar.MONTH);
                if (current_month > last_login_month){
                    db.deleteAllNotifications("personal", db.getPerson().getKey(), new Database.DatabaseCallBack() {
                        @Override
                        public void onSuccess(String success) {
                            db.deleteAllPersonalExpenses(new Database.DatabaseCallBack() {
                                @Override
                                public void onSuccess(String success) {
                                    db.createNotification("personal",
                                            db.getPerson().getKey(),
                                            "monthly_expense",
                                            db.getPerson().getImage(),
                                            String.valueOf(totExpense),
                                            new Database.DatabaseCallBack() {
                                                @Override
                                                public void onSuccess(String success) {
                                                    Log.d(TAG, success);
                                                    db.setLastMonthSaving(budget - totExpense);
                                                }
                                                @Override
                                                public void onError(String error_tag, String error) { Log.e(error_tag, error); }
                                            });
                                }

                                @Override
                                public void onError(String error_tag, String error) {
                                    Log.e(error_tag, error);
                                }
                            });
                        }

                        @Override
                        public void onError(String error_tag, String error) { Log.e(error_tag, error); }
                    });
                }
            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(error_tag, error);
            }
        });
    }

    private void filterList(String status){
        if(!status.equals(getResources().getString(R.string.title_all))) {
            selectedFilter = status;
            ArrayList<Expense> filteredList = new ArrayList<>();
            for (Expense expense : expenseList) {
                if (expense.getType().toLowerCase().contains(status)) {
                    filteredList.add(expense);
                }
            }
            adapter = new ExpensesAdapter(this.getContext(), filteredList);
            recyclerView.setAdapter(adapter);
        }
        else{
            adapter = new ExpensesAdapter(this.getContext(), expenseList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        type = country[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==addExpense.getId()){
            if(!price.getText().toString().trim().isEmpty() && !expenseName.getText().toString().trim().isEmpty()) {
                String name = String.valueOf(expenseName.getText());
                int p = Integer.parseInt(String.valueOf(price.getText()));


                if(type.equals("food") || type.equals("nahrung") || type.equals("yiyecek")){
                    type ="Yiyecek";
                }else if(type.equals("wear") || type.equals("kleidung") || type.equals("giyecek")){
                    type = "Giyecek";
                }else if(type.equals("stationary") || type.equals("schreibwaren") || type.equals("kırtasiye")){
                    type = "Kırtasiye";
                }else if(type.equals("cleaning") || type.equals("reinigungsmittel")|| type.equals("temizlik")){
                    type = "Temizlik";
                }else if(type.equals("others") || type.equals("andere")|| type.equals("diğer")){
                    type = "Diğer";
                }

                db.addExpense(name, type, String.valueOf(p), new Database.DatabaseCallBack() {
                    @Override
                    public void onSuccess(String success) {
                        db.getExpenses(expenseCallBack);
                    }

                    @Override
                    public void onError(String error_tag, String error) { }
                });
            }
        }else if(v.getId()==progressText.getId()){
            addBudgetDialog();
        }
    }

    private void addBudgetDialog() {
        @SuppressLint("InflateParams") final View DialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog_add_budget, null);
        final Dialog dialog = new Dialog(this.getContext(), R.style.DialogAddBudget);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(DialogView);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();

        dialog.findViewById(R.id.add_budget_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText budgetEdit = dialog.findViewById(R.id.add_budget_edit_txt);
                if(!budgetEdit.getText().toString().matches("")){
                    dialog.cancel();
                    budget=Integer.parseInt(String.valueOf(budgetEdit.getText()));
                    update();
                    db.setBudget(budget);
                }else{
                    Log.d(TAG, "Budget eklema dialogunda edit text hatalı");
                }
            }
        });

    }

    public void checkBudget(Integer i){
        if(i == null) {
            addBudgetDialog();
        }else{
            budget = i;
            update();
        }
    }

    private void checkOverBudget(){
        if (totExpense > budget){
            db.checkNotificationExists("personal", db.getPerson().getKey(),
                    "over_budget", new Database.DatabaseCallBack() {
                @Override
                public void onSuccess(String success) {
                    db.changeNotification("personal", db.getPerson().getKey(),
                            "over_budget", String.valueOf(budget - totExpense),
                            new Database.DatabaseCallBack() {
                        @Override
                        public void onSuccess(String success) {
                            Log.d(TAG, success);
                        }

                        @Override
                        public void onError(String error_tag, String error) { Log.e(error_tag, error);}
                    });
                }

                @Override
                public void onError(String error_tag, String error) {
                    Log.w(error_tag, error);
                    db.createNotification("personal", db.getPerson().getKey(),
                            "over_budget", db.getPerson().getImage(),
                            String.valueOf(budget - totExpense), new Database.DatabaseCallBack() {
                        @Override
                        public void onSuccess(String success) { Log.d(TAG, success); }

                        @Override
                        public void onError(String error_tag, String error) { Log.e(error_tag, error); }
                    });
                }
            });
        }
    }

    public void update(){
        progressText.setText(getString(R.string.budget, budget));
        progressBar.setMax(budget);
        remainingbudget.setText(getString(R.string.remaining_budget, (budget - totExpense)));

        if((budget - totExpense)<=0){
            control=true;
            personstatus.setImageResource(R.drawable.circle_background_red);
            progressBar.getProgressDrawable().setColorFilter(
                    getResources().getColor(R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if((budget - totExpense)>0){
            personstatus.setImageResource(R.drawable.circle_background_green);
            if(control==true){
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_custom));
            }
            control=false;
        }

        progressBar.setProgress(totExpense);
        adapter = new ExpensesAdapter(this.getContext(), expenseList);
        recyclerView.setAdapter(adapter);
    }
}