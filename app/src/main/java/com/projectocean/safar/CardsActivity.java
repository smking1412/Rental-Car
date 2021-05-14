package com.projectocean.safar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.safar.models.CardData;
import com.projectocean.safar.viewHolders.CardDataViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CardsActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    RecyclerView itemList;
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        uid = mAuth.getUid();

        floatingActionButton = findViewById(R.id.add_new_card);

        itemList = findViewById(R.id.recycler_view);
        itemList.setHasFixedSize(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        itemList.setLayoutManager(mLayoutManager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Query q = db.getReference("SavedCards").child(uid);

        firebaseSearch(q);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        CardsActivity.this, R.style.BottomSheetDialogTheme
                );

                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.add_card_bottom_sheet,
                                (ScrollView) findViewById(R.id.bottom_sheet_container)
                        );

                final EditText ETcardNumber = bottomSheetView.findViewById(R.id.card_number);
                final EditText ETmm = bottomSheetView.findViewById(R.id.month);
                final EditText ETyy = bottomSheetView.findViewById(R.id.year);
                final EditText ETcvv = bottomSheetView.findViewById(R.id.cvv);
                final EditText ETname = bottomSheetView.findViewById(R.id.name);

                Button add = bottomSheetView.findViewById(R.id.btn_add);

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = ETname.getText().toString();
                        String year = ETyy.getText().toString();
                        String month = ETmm.getText().toString();
                        String cvv = ETcvv.getText().toString();
                        String cardnumber = ETcardNumber.getText().toString();

                        if (name.isEmpty()) {
                            Toast.makeText(CardsActivity.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                        } else if (year.isEmpty() || Integer.parseInt(year) < 20) {

                            Toast.makeText(CardsActivity.this, "Invalid year", Toast.LENGTH_SHORT).show();

                        } else if (month.isEmpty() || Integer.parseInt(month) > 12) {

                            Toast.makeText(CardsActivity.this, "Invalid Month", Toast.LENGTH_SHORT).show();

                        } else if (cvv.isEmpty() || Integer.parseInt(cvv) <= 100) {

                            Toast.makeText(CardsActivity.this, "CVV should be 3 digits", Toast.LENGTH_SHORT).show();

                        } else if (cardnumber.length() != 19) {

                            Toast.makeText(CardsActivity.this, "Invalid card Number", Toast.LENGTH_SHORT).show();

                        } else {
                            String key = db.getReference("SavedCards").child(uid).push().getKey() + "";

                            CardData cardData = new CardData(cardnumber, name, key, Integer.parseInt(month), Integer.parseInt(year), Integer.parseInt(cvv));

                            db.getReference("SavedCards").child(uid).child(key).setValue(cardData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        bottomSheetDialog.dismiss();
                                        Toast.makeText(CardsActivity.this, "added", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CardsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                    }
                });

                ETcardNumber.addTextChangedListener(new TextWatcher() {

                    private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
                    private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
                    private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
                    private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
                    private static final char DIVIDER = '-';

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // noop
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // noop
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                            s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                        }
                    }

                    private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                        boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                        for (int i = 0; i < s.length(); i++) { // check that every element is right
                            if (i > 0 && (i + 1) % dividerModulo == 0) {
                                isCorrect &= divider == s.charAt(i);
                            } else {
                                isCorrect &= Character.isDigit(s.charAt(i));
                            }
                        }
                        return isCorrect;
                    }

                    private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                        final StringBuilder formatted = new StringBuilder();

                        for (int i = 0; i < digits.length; i++) {
                            if (digits[i] != 0) {
                                formatted.append(digits[i]);
                                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                                    formatted.append(divider);
                                }
                            }
                        }

                        return formatted.toString();
                    }

                    private char[] getDigitArray(final Editable s, final int size) {
                        char[] digits = new char[size];
                        int index = 0;
                        for (int i = 0; i < s.length() && index < size; i++) {
                            char current = s.charAt(i);
                            if (Character.isDigit(current)) {
                                digits[index] = current;
                                index++;
                            }
                        }
                        return digits;
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
    }

    public void firebaseSearch(Query q) {

        Log.d("recycle debug", "firebasesearch");

        FirebaseRecyclerAdapter<CardData, CardDataViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<CardData, CardDataViewHolder>(
                CardData.class, R.layout.cardlayout, CardDataViewHolder.class, q
        ) {

            @Override
            protected void populateViewHolder(final CardDataViewHolder viewHolder, final CardData model, final int position) {


                viewHolder.setDetails(model.getCardNumber(), model.getExpMonth(), model.getExpYear(), model.getCardHolderName(), model.getCvv());

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        PopupMenu popup = new PopupMenu(getApplicationContext(), viewHolder.chip);
                        popup.inflate(R.menu.more_menu);

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                if (menuItem.getItemId() == R.id.edit) {
                                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                            CardsActivity.this, R.style.BottomSheetDialogTheme
                                    );

                                    View bottomSheetView = LayoutInflater.from(getApplicationContext())
                                            .inflate(
                                                    R.layout.add_card_bottom_sheet,
                                                    (ScrollView) findViewById(R.id.bottom_sheet_container)
                                            );

//                final RadioGroup radioGroup = bottomSheetView.findViewById(R.id.radio_group);
//
//                Button buttonContinue = bottomSheetView.findViewById(R.id.continue_pay);

                                    final EditText ETcardNumber = bottomSheetView.findViewById(R.id.card_number);
                                    final EditText ETmm = bottomSheetView.findViewById(R.id.month);
                                    final EditText ETyy = bottomSheetView.findViewById(R.id.year);
                                    final EditText ETcvv = bottomSheetView.findViewById(R.id.cvv);
                                    final EditText ETname = bottomSheetView.findViewById(R.id.name);
                                    final TextView ETlabel = bottomSheetView.findViewById(R.id.add_label);

                                    ETcardNumber.setText(model.getCardNumber());
                                    ETmm.setText(model.getExpMonth().toString());
                                    ETyy.setText(model.getExpYear().toString());
                                    ETcvv.setText(model.getCvv().toString());
                                    ETname.setText(model.getCardHolderName());
                                    ETlabel.setText("Edit Your Card");

                                    Button add = bottomSheetView.findViewById(R.id.btn_add);

                                    add.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String name = ETname.getText().toString();
                                            String year = ETyy.getText().toString();
                                            String month = ETmm.getText().toString();
                                            String cvv = ETcvv.getText().toString();
                                            String cardnumber = ETcardNumber.getText().toString();


                                            if (name.isEmpty()) {

                                            } else if (year.isEmpty() || Integer.parseInt(year) < 20) {

                                            } else if (month.isEmpty() || Integer.parseInt(month) > 12) {

                                            } else if (cvv.isEmpty() || Integer.parseInt(cvv) <= 100) {

                                            } else if (cardnumber.length() != 19) {

                                            } else {
                                                String key = model.getId();

                                                CardData cardData = new CardData(cardnumber, name, key, Integer.parseInt(month), Integer.parseInt(year), Integer.parseInt(cvv));

                                                db.getReference("SavedCards").child(uid).child(key).setValue(cardData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            bottomSheetDialog.dismiss();
                                                            Toast.makeText(CardsActivity.this, "added", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(CardsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }

                                        }
                                    });

                                    ETcardNumber.addTextChangedListener(new TextWatcher() {

                                        private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
                                        private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
                                        private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
                                        private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
                                        private static final char DIVIDER = '-';

                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                            // noop
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            // noop
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                                                s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                                            }
                                        }

                                        private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                                            boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                                            for (int i = 0; i < s.length(); i++) { // check that every element is right
                                                if (i > 0 && (i + 1) % dividerModulo == 0) {
                                                    isCorrect &= divider == s.charAt(i);
                                                } else {
                                                    isCorrect &= Character.isDigit(s.charAt(i));
                                                }
                                            }
                                            return isCorrect;
                                        }

                                        private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                                            final StringBuilder formatted = new StringBuilder();

                                            for (int i = 0; i < digits.length; i++) {
                                                if (digits[i] != 0) {
                                                    formatted.append(digits[i]);
                                                    if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                                                        formatted.append(divider);
                                                    }
                                                }
                                            }

                                            return formatted.toString();
                                        }

                                        private char[] getDigitArray(final Editable s, final int size) {
                                            char[] digits = new char[size];
                                            int index = 0;
                                            for (int i = 0; i < s.length() && index < size; i++) {
                                                char current = s.charAt(i);
                                                if (Character.isDigit(current)) {
                                                    digits[index] = current;
                                                    index++;
                                                }
                                            }
                                            return digits;
                                        }
                                    });

                                    bottomSheetDialog.setContentView(bottomSheetView);
                                    bottomSheetDialog.show();
                                } else if (menuItem.getItemId() == R.id.delete) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(CardsActivity.this);
                                    builder1.setMessage("Do you want to delete this card?");
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    db.getReference("SavedCards").child(uid).child(model.getId()).removeValue();
                                                }
                                            });

                                    builder1.setNegativeButton(
                                            "No",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }
                                    );

                                    AlertDialog alert = builder1.create();
                                    alert.show();
                                }
                                return false;
                            }
                        });

                        popup.show();
                        return false;
                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                CardsActivity.this, R.style.BottomSheetDialogTheme
                        );

                        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                                .inflate(
                                        R.layout.add_card_bottom_sheet,
                                        (ScrollView) findViewById(R.id.bottom_sheet_container)
                                );

//                final RadioGroup radioGroup = bottomSheetView.findViewById(R.id.radio_group);
//
//                Button buttonContinue = bottomSheetView.findViewById(R.id.continue_pay);

                        final EditText ETcardNumber = bottomSheetView.findViewById(R.id.card_number);
                        final EditText ETmm = bottomSheetView.findViewById(R.id.month);
                        final EditText ETyy = bottomSheetView.findViewById(R.id.year);
                        final EditText ETcvv = bottomSheetView.findViewById(R.id.cvv);
                        final EditText ETname = bottomSheetView.findViewById(R.id.name);
                        final TextView ETlabel = bottomSheetView.findViewById(R.id.add_label);


                        ETcardNumber.setText(model.getCardNumber());
                        ETmm.setText(model.getExpMonth().toString());
                        ETyy.setText(model.getExpYear().toString());
                        ETcvv.setText(model.getCvv().toString());
                        ETname.setText(model.getCardHolderName());
                        ETlabel.setText("Edit Your Card");

                        Button add = bottomSheetView.findViewById(R.id.btn_add);

                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = ETname.getText().toString();
                                String year = ETyy.getText().toString();
                                String month = ETmm.getText().toString();
                                String cvv = ETcvv.getText().toString();
                                String cardnumber = ETcardNumber.getText().toString();

                                if (name.isEmpty()) {

                                } else if (year.isEmpty() || Integer.parseInt(year) < 20) {

                                } else if (month.isEmpty() || Integer.parseInt(month) > 12) {

                                } else if (cvv.isEmpty() || Integer.parseInt(cvv) <= 100) {

                                } else if (cardnumber.length() != 19) {

                                } else {
                                    String key = model.getId();

                                    CardData cardData = new CardData(cardnumber, name, key, Integer.parseInt(month), Integer.parseInt(year), Integer.parseInt(cvv));

                                    db.getReference("SavedCards").child(uid).child(key).setValue(cardData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                bottomSheetDialog.dismiss();
                                                Toast.makeText(CardsActivity.this, "added", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(CardsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }

                            }
                        });

                        ETcardNumber.addTextChangedListener(new TextWatcher() {

                            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
                            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
                            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
                            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
                            private static final char DIVIDER = '-';

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                // noop
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                // noop
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                                }
                            }

                            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                                for (int i = 0; i < s.length(); i++) { // check that every element is right
                                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                                        isCorrect &= divider == s.charAt(i);
                                    } else {
                                        isCorrect &= Character.isDigit(s.charAt(i));
                                    }
                                }
                                return isCorrect;
                            }

                            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                                final StringBuilder formatted = new StringBuilder();

                                for (int i = 0; i < digits.length; i++) {
                                    if (digits[i] != 0) {
                                        formatted.append(digits[i]);
                                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                                            formatted.append(divider);
                                        }
                                    }
                                }

                                return formatted.toString();
                            }

                            private char[] getDigitArray(final Editable s, final int size) {
                                char[] digits = new char[size];
                                int index = 0;
                                for (int i = 0; i < s.length() && index < size; i++) {
                                    char current = s.charAt(i);
                                    if (Character.isDigit(current)) {
                                        digits[index] = current;
                                        index++;
                                    }
                                }
                                return digits;
                            }
                        });

                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();

                    }
                });

            }
        };


        itemList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
