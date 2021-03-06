package com.beautypop.activity;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.adapter.MessageListAdapter;
import com.beautypop.app.AppController;
import com.beautypop.app.ConversationCache;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.SelectedImage;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.ConversationOrderVM;
import com.beautypop.viewmodel.ConversationVM;
import com.beautypop.viewmodel.MessageVM;
import com.beautypop.viewmodel.NewConversationOrderVM;
import com.beautypop.viewmodel.NewMessageVM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MessageListActivity extends TrackedFragmentActivity {
    private static final String TAG = MessageListActivity.class.getName();

    private TextView postTitleText, postPriceText, commentEdit;
    private FrameLayout mainFrameLayout;
    private EditText commentEditText;
    private PopupWindow commentPopup;

    private TextView titleText;
    private ImageView backImage, profileButton, postImage;

    private ListView listView;
    private View listHeader;
    private RelativeLayout postLayout, alertLayout, loadMoreLayout;

    private TextView alertText, commentSendButton;

    private LinearLayout buyerButtonsLayout, buyerOrderLayout, buyerCancelLayout, buyerMessageLayout;
    private Button buyerOrderButton, buyerCancelButton, buyerOrderAgainButton, buyerMessageButton;

    private LinearLayout sellerButtonsLayout, sellerAcceptDeclineLayout, sellerMessageLayout;
    private Button sellerAcceptButton, sellerDeclineButton, sellerMessageButton, buyerReviewButton, sellerReviewButton;

    private List<MessageVM> messages = new ArrayList<>();
    private MessageListAdapter adapter;

    private List<ImageView> commentImages = new ArrayList<>();

    private Uri selectedImageUri = null;
    private List<SelectedImage> selectedImages = new ArrayList<>();

    private ConversationVM conversation;
    private Long offset = 1L;

    private boolean pending = false;
    private boolean pendingOrder = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_list_activity);

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        listHeader = layoutInflater.inflate(R.layout.message_list_header, null);
        loadMoreLayout = (RelativeLayout) listHeader.findViewById(R.id.loadMoreLayout);

        listView = (ListView)findViewById(R.id.messageList);

        postLayout = (RelativeLayout) findViewById(R.id.postLayout);
        postImage = (ImageView) findViewById(R.id.postImage);
        postTitleText = (TextView) findViewById(R.id.postTitleText);
        postPriceText = (TextView) findViewById(R.id.postPriceText);

        alertLayout = (RelativeLayout) findViewById(R.id.alertLayout);
        alertText = (TextView) findViewById(R.id.alertText);

        mainFrameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout);
        profileButton = (ImageView) findViewById(R.id.profileButton);
        commentEdit = (TextView) findViewById(R.id.commentEdit);

        // conversation
        conversation = (ConversationVM) getIntent().getSerializableExtra(ViewUtil.BUNDLE_KEY_OBJECT);

        setToolbarTitle(conversation.getUserName());
        postTitleText.setText(conversation.getPostTitle());
        postPriceText.setText(ViewUtil.formatPrice(conversation.getPostPrice()));
        ImageUtil.displayPostImage(conversation.getPostImage(), postImage);

        listView.addHeaderView(listHeader);
        listHeader.setVisibility(View.INVISIBLE);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final MessageVM item = adapter.getItem(i);
                if (ViewUtil.copyToClipboard(item.getBody())) {
                    Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.text_copy_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.text_copy_failed), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startUserProfileActivity(MessageListActivity.this, conversation.getUserId());
            }
        });

        postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startProductActivity(MessageListActivity.this, conversation.getPostId());
            }
        });

        if (conversation.isUserLowReviewScore() || conversation.isUserSuspended()) {
            String alertMessage = getString(R.string.alert_user_low_review_score, conversation.getUserName());
            if (conversation.isUserSuspended()) {
                alertMessage = getString(R.string.alert_user_suspended, conversation.getUserName());
            }

            alertText.setText(alertMessage);
            alertLayout.setVisibility(View.VISIBLE);
        } else {
            alertLayout.setVisibility(View.GONE);
        }

        loadMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreMessages(conversation.id, offset);
                offset++;
            }
        });

        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCommentPopup();
            }
        });

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buyerButtonsLayout = (LinearLayout) findViewById(R.id.buyerButtonsLayout);
        buyerOrderLayout = (LinearLayout) findViewById(R.id.buyerOrderLayout);
        buyerOrderButton = (Button) findViewById(R.id.buyerOrderButton);
        buyerCancelLayout = (LinearLayout) findViewById(R.id.buyerCancelLayout);
        buyerCancelButton = (Button) findViewById(R.id.buyerCancelButton);
        buyerMessageLayout = (LinearLayout) findViewById(R.id.buyerMessageLayout);
        buyerMessageButton = (Button) findViewById(R.id.buyerMessageButton);
        buyerOrderAgainButton = (Button) findViewById(R.id.buyerOrderAgainButton);

        sellerButtonsLayout = (LinearLayout) findViewById(R.id.sellerButtonsLayout);
        sellerAcceptDeclineLayout = (LinearLayout) findViewById(R.id.sellerAcceptDeclineLayout);
        sellerAcceptButton = (Button) findViewById(R.id.sellerAcceptButton);
        sellerDeclineButton = (Button) findViewById(R.id.sellerDeclineButton);
        sellerMessageLayout = (LinearLayout) findViewById(R.id.sellerMessageLayout);
        sellerMessageButton = (Button) findViewById(R.id.sellerMessageButton);
        buyerReviewButton = (Button) findViewById(R.id.buyerReviewButton);
		sellerReviewButton = (Button) findViewById(R.id.sellerReviewButton);

		buyerReviewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MessageListActivity.this,LeaveReviewActivity.class);
				intent.putExtra(ViewUtil.BUNDLE_KEY_ID,conversation.getOrder().getId());
				intent.putExtra(ViewUtil.BUNDLE_KEY_ARG1,true);
				startActivity(intent);
			}
		});

		sellerReviewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MessageListActivity.this,LeaveReviewActivity.class);
				intent.putExtra(ViewUtil.BUNDLE_KEY_ID,conversation.getOrder().getId());
				intent.putExtra(ViewUtil.BUNDLE_KEY_ARG1,false);
				startActivity(intent);
			}
		});

        initLayout(conversation);
        loadMessages(conversation);
    }

    private void initLayout(final ConversationVM conversation) {
        ConversationOrderVM order = conversation.getOrder();

        ViewUtil.setConversationImageTag(this, conversation);

        // action buttons
        boolean isBuyer = !conversation.postOwner;
        buyerButtonsLayout.setVisibility(isBuyer? View.VISIBLE : View.GONE);
        sellerButtonsLayout.setVisibility(isBuyer? View.GONE : View.VISIBLE);

        // show actions based on order state
        if (isBuyer) {
            initBuyerLayout(conversation, order);
        } else {
            initSellerLayout(conversation, order);
        }
    }

    private void initBuyerLayout(final ConversationVM conversation, final ConversationOrderVM order) {
        buyerOrderLayout.setVisibility(View.GONE);
        buyerCancelLayout.setVisibility(View.GONE);
        buyerMessageLayout.setVisibility(View.GONE);

        if (order == null) {    // no order yet
            if (conversation.postSold) {
                buyerButtonsLayout.setVisibility(View.GONE);
                sellerButtonsLayout.setVisibility(View.GONE);
            } else {
                buyerOrderLayout.setVisibility(View.VISIBLE);
                buyerOrderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final long price = (long) conversation.getPostPrice();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessageListActivity.this);
                        final EditText priceEdit = ViewUtil.initOfferPriceAlertLayout(MessageListActivity.this, price, alertDialogBuilder);
                        alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long offeredPrice = ViewUtil.validateAndGetPriceFromInput(MessageListActivity.this, priceEdit);
                                if (offeredPrice != -1L) {
                                    doBuyerOrder(conversation, offeredPrice);
                                }
                            }
                        });
                        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            }
        } else if (!order.closed) {     // open orders
            buyerCancelLayout.setVisibility(View.VISIBLE);
            buyerCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessageListActivity.this);
                    alertDialogBuilder.setMessage(getString(R.string.pm_order_cancel_confirm));
                    alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doBuyerCancel(conversation);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        } else {    // closed orders
            buyerMessageLayout.setVisibility(View.VISIBLE);
            buyerOrderAgainButton.setVisibility(View.VISIBLE);
            buyerReviewButton.setVisibility(View.GONE);

            if (conversation.postSold) {
                buyerOrderAgainButton.setVisibility(View.GONE);
            }

            if (order.accepted) {
                buyerMessageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_accept, 0, 0, 0);
                buyerMessageButton.setText(getString(R.string.pm_order_accepted_for_buyer));
                buyerOrderAgainButton.setVisibility(View.GONE);
				buyerReviewButton.setVisibility(View.VISIBLE);
            } else if (order.declined) {
                buyerMessageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_decline, 0, 0, 0);
                buyerMessageButton.setText(getString(R.string.pm_order_declined_for_buyer));
            } else if (order.cancelled) {
                buyerMessageButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                buyerMessageButton.setText(getString(R.string.pm_order_cancelled));
            }

            buyerOrderAgainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final long price = (long) conversation.getPostPrice();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessageListActivity.this);
                    final EditText priceEdit = ViewUtil.initOfferPriceAlertLayout(MessageListActivity.this, price, alertDialogBuilder);
                    alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long offeredPrice = ViewUtil.validateAndGetPriceFromInput(MessageListActivity.this, priceEdit);
                            if (offeredPrice != -1L) {
                                doBuyerOrder(conversation, offeredPrice);
                            }
                        }
                    });
                    alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }
    }

    private void initSellerLayout(final ConversationVM conversation, final ConversationOrderVM order) {
        sellerAcceptDeclineLayout.setVisibility(View.GONE);
        sellerMessageLayout.setVisibility(View.GONE);
        if (order == null) {    // no order yet
            // no actions... hide seller actions
            sellerButtonsLayout.setVisibility(View.GONE);
        } else if (!order.closed) {     // open orders
            sellerAcceptDeclineLayout.setVisibility(View.VISIBLE);
            sellerAcceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessageListActivity.this);
                    alertDialogBuilder.setMessage(getString(R.string.pm_order_accept_confirm));
                    alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doSellerAccept(conversation);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
            sellerDeclineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MessageListActivity.this);
                    alertDialogBuilder.setMessage(getString(R.string.pm_order_decline_confirm));
                    alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doSellerDecline(conversation);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        } else {    // closed orders
            sellerMessageLayout.setVisibility(View.VISIBLE);
            sellerReviewButton.setVisibility(View.GONE);

            if (order.accepted) {
                sellerMessageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_accept, 0, 0, 0);
                sellerMessageButton.setText(getString(R.string.pm_order_accepted_for_seller));
                sellerReviewButton.setVisibility(View.VISIBLE);
            } else if (order.declined) {
                sellerMessageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_decline, 0, 0, 0);
                sellerMessageButton.setText(getString(R.string.pm_order_declined_for_seller));
            } else if (order.cancelled) {
                sellerButtonsLayout.setVisibility(View.GONE);
            }
        }
    }

    private void initCommentPopup() {
        //mainFrameLayout.getForeground().setAlpha(20);
        //mainFrameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

        try {
            LayoutInflater inflater = (LayoutInflater) MessageListActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View layout = inflater.inflate(R.layout.message_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            if (commentPopup == null) {
                commentPopup = new PopupWindow(
                        layout,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, //activityUtil.getRealDimension(DefaultValues.COMMENT_POPUP_HEIGHT),
                        true);

                commentPopup.setOutsideTouchable(false);
                commentPopup.setFocusable(true);
                commentPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
                commentPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                commentPopup.setTouchInterceptor(new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent event) {
                        return false;
                    }
                });

                commentEditText = (EditText) layout.findViewById(R.id.commentEditText);
                commentEditText.setLongClickable(true);

                // NOTE: UGLY WORKAROUND or pasting text to comment edit!!!
                commentEditText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.d(TAG, "onLongClick");
                        startActionMode(new ActionMode.Callback() {

                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                Log.d(TAG, "onPrepareActionMode");
                                return true;
                            }

                            public void onDestroyActionMode(ActionMode mode) {
                            }

                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
                                menu.setQwertyMode(false);
                                Log.d(TAG, "onCreateActionMode: menu size=" + menu.size());
                                return true;
                            }

                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                Log.d(TAG, "onActionItemClicked: item clicked=" + item.getItemId() + " title=" + item.getTitle());
                                switch (item.getItemId()) {
                                    case R.id.item_select_all:
                                        commentEditText.selectAll();
                                        break;

                                    case R.id.item_cut:
                                        ViewUtil.copyToClipboard(commentEditText.getText().toString());
                                        commentEditText.getText().clear();
                                        mode.finish();
                                        break;

                                    case R.id.item_paste:
                                        final ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                        if (clipBoard != null && clipBoard.getPrimaryClip() != null && clipBoard.getPrimaryClip().getItemAt(0) != null) {
                                            String paste = clipBoard.getPrimaryClip().getItemAt(0).getText().toString();
                                            commentEditText.getText().insert(commentEditText.getSelectionStart(), paste);
                                        }
                                        mode.finish();
                                        break;
                                }

                                // popup again
                                commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
                                ViewUtil.popupInputMethodWindow(MessageListActivity.this, commentEditText);
                                return true;
                            }
                        });
                        return true;
                    }
                });

                /*
                commentEditText.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if( keyCode == KeyEvent.KEYCODE_MENU ){
                            return false;
                        }
                        return false;
                    }
                });
                */

                /*
                commentEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        Log.d(TAG, "onPrepareActionMode");
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode mode) {
                    }

                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        Log.d(TAG, "onCreateActionMode");
                        return false;
                    }

                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        Log.d(TAG, "onActionItemClicked");
                        return false;
                    }
                });
                */

                commentSendButton = (TextView) layout.findViewById(R.id.commentSendButton);
                commentSendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doMessage();
                    }
                });

                ImageView commentCancelButton = (ImageView) layout.findViewById(R.id.commentCancelButton);
                commentCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentPopup.dismiss();
                        commentPopup = null;
                    }
                });

                ImageView commentBrowseButton = (ImageView) layout.findViewById(R.id.browseImage);
                commentBrowseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageUtil.openPhotoPicker(MessageListActivity.this);
                    }
                });

                if (commentImages.size() == 0) {
                    commentImages.add((ImageView) layout.findViewById(R.id.commentImage));

                    for (ImageView commentImage : commentImages) {
                        commentImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeSelectedCommentImage();
                            }
                        });
                    }
                }
            }

            commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            ViewUtil.popupInputMethodWindow(this, commentEditText);
        } catch (Exception e) {
            Log.e(TAG, "initCommentPopup: exception", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (selectedImages.size() >= DefaultValues.MAX_MESSAGE_IMAGES) {
                //Toast.makeText(MessageListActivity.this,
                //        String.format(MessageListActivity.this.getString(R.string.pm_max_images), DefaultValues.MAX_MESSAGE_IMAGES), Toast.LENGTH_SHORT).show();
                //return;

                selectedImages.clear();
            }

            if ( (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) ||
                    requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE )  {

                String imagePath = "";
                if (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) {
                    selectedImageUri = data.getData();
                    imagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);
                } else if (requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE) {
                    File picture = new File(ImageUtil.CAMERA_IMAGE_TEMP_PATH);
                    selectedImageUri = Uri.fromFile(picture);
                    imagePath = picture.getPath();
                }

                Log.d(TAG, "onActivityResult: imagePath=" + imagePath);

                Bitmap bitmap = ImageUtil.resizeToUpload(imagePath);
                if (bitmap != null) {
                    ViewUtil.startSelectImageActivity(this, selectedImageUri);
                } else {
                    Toast.makeText(this, getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == ViewUtil.CROP_IMAGE_REQUEST_CODE) {
                String croppedImagePath = data.getStringExtra(ViewUtil.INTENT_RESULT_OBJECT);
                Log.d(TAG, "onActivityResult: croppedImagePath=" + croppedImagePath);

                // adjusted?
                if (data.getData() != null) {
                    selectedImageUri = data.getData();
                    croppedImagePath = ImageUtil.getRealPathFromUri(this, data.getData());
                }

                setCommentImage(croppedImagePath);

                /*
                if (data.getData() != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        selectedImageUri = data.getData();
                        setCommentImage(bitmap);
                    } catch (IOException e) {
                        Log.e(TAG, "onActivityResult: crop image error", e);
                    }
                } else {
                    setCommentImage(croppedImagePath);
                }
                */
            }

            // pop back soft keyboard
            //ViewUtil.popupInputMethodWindow(this);
        }
    }

    private void setCommentImage(Bitmap bp) {
        ImageView postImage = commentImages.get(0);
        postImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
        postImage.setVisibility(View.VISIBLE);
        selectedImages.add(new SelectedImage(0, ImageUtil.getRealPathFromUri(this, selectedImageUri)));
    }

    private void setCommentImage(String imagePath) {
        Log.d(TAG, "setCommentImage: imagePath=" + imagePath);
        if (!StringUtils.isEmpty(imagePath)) {
            selectedImages.add(new SelectedImage(0, imagePath));

            ImageView imageView = commentImages.get(0);
            Bitmap bp = ImageUtil.resizeAsPreviewThumbnail(imagePath);
            imageView.setImageBitmap(bp);
            //imageView.setImageURI(Uri.parse(imagePath);
        }
    }

    private void removeSelectedCommentImage() {
        SelectedImage toRemove = getSelectedCommentImage(0);
        selectedImages.remove(toRemove);
        commentImages.get(0).setImageDrawable(null);
    }

    private SelectedImage getSelectedCommentImage(int index) {
        for (SelectedImage image : selectedImages) {
            if (image.index.equals(index)) {
                return image;
            }
        }
        return null;
    }

    private void doBuyerOrder(final ConversationVM conversation, final long offeredPrice) {
        ConversationOrderVM order = conversation.getOrder();
        if (order != null && !order.closed) {
            Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.pm_order_already), Toast.LENGTH_SHORT).show();
            return;
        }

        if (pendingOrder) {
            return;
        }

        pendingOrder = true;
        doMessage(getString(R.string.pm_buy_message)+": $"+offeredPrice, true);
        final NewConversationOrderVM newConversationOrder = new NewConversationOrderVM(conversation.id, offeredPrice);
        AppController.getApiService().newConversationOrder(newConversationOrder, new Callback<ConversationOrderVM>() {
            @Override
            public void success(ConversationOrderVM order, Response response) {
                ConversationVM updatedConversation = ConversationCache.updateConversationOrder(conversation.id, order);
                initLayout(updatedConversation);

                pendingOrder = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "doBuyerOrder.api.newConversationOrder: failed with error", error);
                Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.pm_order_failed), Toast.LENGTH_SHORT).show();
                pendingOrder = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }
        });
    }

    private void doBuyerCancel(final ConversationVM conversation) {
        ConversationOrderVM order = conversation.getOrder();
        if (order != null && order.closed) {
            Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.pm_order_already_closed), Toast.LENGTH_SHORT).show();
            return;
        }

        if (pendingOrder) {
            return;
        }

        pendingOrder = true;
        doMessage(getString(R.string.pm_cancelled_message), true);
        AppController.getApiService().cancelConversationOrder(conversation.getOrder().id, new Callback<ConversationOrderVM>() {
            @Override
            public void success(ConversationOrderVM order, Response response) {
                ConversationVM updatedConversation = ConversationCache.updateConversationOrder(conversation.id, order);
                initLayout(updatedConversation);

                pendingOrder = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "doBuyerCancel.api.cancelConversationOrder: failed with error", error);
                Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.pm_order_failed), Toast.LENGTH_SHORT).show();
                pendingOrder = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }
        });
    }

    private void doSellerAccept(final ConversationVM conversation) {
        ConversationOrderVM order = conversation.getOrder();
        if (order != null && order.closed) {
            Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.pm_order_already_closed), Toast.LENGTH_SHORT).show();
            return;
        }

        if (pendingOrder) {
            return;
        }

        pendingOrder = true;
        doMessage(getString(R.string.pm_accepted_message), true);
        AppController.getApiService().acceptConversationOrder(conversation.getOrder().id, new Callback<ConversationOrderVM>() {
            @Override
            public void success(ConversationOrderVM order, Response response) {
                ConversationVM updatedConversation = ConversationCache.updateConversationOrder(conversation.id, order);
                initLayout(updatedConversation);

                pendingOrder = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "doSellerAccept.api.acceptConversationOrder: failed with error", error);
                Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.pm_order_failed), Toast.LENGTH_SHORT).show();
                pendingOrder = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }
        });
    }

    private void doSellerDecline(final ConversationVM conversation) {
        ConversationOrderVM order = conversation.getOrder();
        if (order != null && order.closed) {
            Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.pm_order_already_closed), Toast.LENGTH_SHORT).show();
            return;
        }

        if (pendingOrder) {
            return;
        }

        pendingOrder = true;
        doMessage(getString(R.string.pm_declined_message), true);
        AppController.getApiService().declineConversationOrder(conversation.getOrder().id, new Callback<ConversationOrderVM>() {
            @Override
            public void success(ConversationOrderVM order, Response response) {
                ConversationVM updatedConversation = ConversationCache.updateConversationOrder(conversation.id, order);
                initLayout(updatedConversation);

                pendingOrder = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "doSellerDecline.api.declineConversationOrder: failed with error", error);
                Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.pm_order_failed), Toast.LENGTH_SHORT).show();
                pendingOrder = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }
        });

    }

    private void doMessage() {
        final boolean withPhotos = selectedImages.size() > 0;
        String body = commentEditText.getText().toString().trim();
        if (StringUtils.isEmpty(body) && !withPhotos) {
            Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.invalid_comment_body_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        doMessage(body);
    }

    private void doMessage(String message) {
        doMessage(message, false);
    }

    private void doMessage(String message, boolean system) {
        if (pending) {
            return;
        }

        //Log.d(TAG, "doMessage: message=" + comment.substring(0, Math.min(5, comment.length())));

        ViewUtil.showSpinner(MessageListActivity.this);

        pending = true;
        NewMessageVM newMessage = new NewMessageVM(conversation.id, message, system, selectedImages);
        AppController.getApiService().newMessage(newMessage, new Callback<MessageVM>() {
            @Override
            public void success(MessageVM message, Response response) {
                messages.add(message);
                adapter.notifyDataSetChanged();
                listView.smoothScrollToPosition(messages.size());

                ViewUtil.setActivityResult(MessageListActivity.this, conversation.id);
                reset();
                pending = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "doMessage.api.newMessage: failed with error", error);
                Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.pm_send_failed), Toast.LENGTH_SHORT).show();
                reset();
                pending = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }
        });
    }

    private int parseAndAddMessages(String responseBody) {
        int count = 0;
        try {
            JSONObject obj = new JSONObject(responseBody);

            JSONArray messagesObj = obj.getJSONArray(ViewUtil.JSON_KEY_MESSAGE_KEY);
            count = messagesObj.length();
            for (int i = 0; i < count; i++) {
                JSONObject messageObj = messagesObj.getJSONObject(i);
                Log.d(TAG, "getMessages.api.getMessages: message["+i+"]="+messageObj.toString());
                MessageVM vm = new MessageVM(messageObj);
                messages.add(vm);
            }

            Collections.sort(messages, new Comparator<MessageVM>() {
                public int compare(MessageVM m1, MessageVM m2) {
                    return m1.getCreatedDate().compareTo(m2.getCreatedDate());
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "getMessages.api.getMessages: exception", e);
        }
        return count;
    }

    private void loadMessages(final ConversationVM conversation) {
        ViewUtil.showSpinner(MessageListActivity.this);
        AppController.getApiService().getMessages(conversation.id, 0L, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                listHeader.setVisibility(View.INVISIBLE);

                messages.clear();

                String responseBody = ViewUtil.getResponseBody(responseObject);
                int count = parseAndAddMessages(responseBody);
                if (count >= DefaultValues.CONVERSATION_MESSAGE_COUNT) {
                    listHeader.setVisibility(View.VISIBLE);
                }

                adapter = new MessageListAdapter(MessageListActivity.this, messages, true);
                listView.setAdapter(adapter);

                // send a buy message to seller
                boolean buy = getIntent().getBooleanExtra(ViewUtil.BUNDLE_KEY_ARG1, false);
                long offeredPrice = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ARG2, (long) conversation.getPostPrice());
                if (buy) {
                    doBuyerOrder(conversation, offeredPrice);
                }

                ViewUtil.stopSpinner(MessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(MessageListActivity.this);
                Log.e(TAG, "getMessages.api.getMessages: failure", error);
            }
        });
    }

    private void loadMoreMessages(Long conversationId, Long offset) {
        ViewUtil.showSpinner(MessageListActivity.this);
        AppController.getApiService().getMessages(conversationId, offset, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                listHeader.setVisibility(View.INVISIBLE);

                String responseBody = ViewUtil.getResponseBody(responseObject);
                int count = parseAndAddMessages(responseBody);
                if (count >= DefaultValues.CONVERSATION_MESSAGE_COUNT) {
                    listHeader.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();

                // restore previous listView position
                View childView = listView.getChildAt(0);
                int top = (childView == null)? 0 : (childView.getTop() - listView.getPaddingTop());
                listView.setSelectionFromTop(count, top);

                ViewUtil.stopSpinner(MessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(MessageListActivity.this);
                Log.e(TAG, "loadMoreMessages.api.getMessages: failure", error);
            }
        });
    }

    private void setConversation(ConversationVM conversation) {
        this.conversation = conversation;
    }

    private void reset() {
        selectedImages.clear();

        if (commentPopup != null) {
            commentEditText.setText("");
            commentPopup.dismiss();
            commentPopup = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //startService(intent);
        //registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        //unregisterReceiver(broadcastReceiver);
        //stopService(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reset();
    }
}