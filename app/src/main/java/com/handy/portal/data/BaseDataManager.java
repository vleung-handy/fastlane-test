package com.handy.portal.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.core.Service;
import com.handy.portal.core.User;
import com.handy.portal.core.booking.Booking;
import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public final class BaseDataManager extends DataManager
{
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;
    private final SecurePreferences prefs;
    private final Gson gsonBuilder;

    @Inject
    public BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint,
                           final Bus bus, final SecurePreferences prefs)
    {
        super(bus);
        this.service = service;
        this.endpoint = endpoint;
        this.prefs = prefs;
        this.gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    }

    @Override
    public String getBaseUrl()
    {
        return endpoint.getBaseUrl();
    }

    @Override
    public final void getServices(final CacheResponse<List<Service>> cache,
                                  final Callback<List<Service>> cb)
    {
        final List<Service> cachedServices = new Gson().fromJson(prefs.getString("CACHED_SERVICES"),
                new TypeToken<List<Service>>()
                {
                }.getType());
        cache.onResponse(cachedServices != null ? cachedServices : new ArrayList<Service>());

        final ArrayList<Service> servicesMenu = new ArrayList<>();
        final HashMap<String, Service> menuMap = new HashMap<>();

        service.getServicesMenu(new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                final JSONArray array = response.optJSONArray("menu_structure");

                if (array == null)
                {
                    cb.onError(new DataManagerError(Type.SERVER));
                    return;
                }

                for (int i = 0; i <= array.length(); i++)
                {
                    final JSONObject obj = array.optJSONObject(i);

                    if (obj != null)
                    {
                        final String name = obj.isNull("name") ? null : obj.optString("name", null);
                        final int ignore = obj.optInt("ignore", 1);

                        if (name == null || ignore == 1) continue;

                        final Service service = new Service();
                        service.setUniq(obj.isNull("uniq") ? null : obj.optString("uniq"));
                        service.setName(obj.isNull("name") ? null : obj.optString("name"));
                        service.setOrder(obj.optInt("order", 0));
                        servicesMenu.add(service);
                        menuMap.put(service.getUniq(), service);
                    }
                }

                final HashMap<Integer, ArrayList<Service>> servicesMap = new HashMap<>();

                service.getServices(new HandyRetrofitCallback(cb)
                {
                    @Override
                    void success(final JSONObject response)
                    {
                        final JSONArray array = response.optJSONArray("services_list");

                        if (array == null)
                        {
                            cb.onError(new DataManagerError(Type.SERVER));
                            return;
                        }

                        for (int i = 0; i <= array.length(); i++)
                        {
                            final JSONObject obj = array.optJSONObject(i);

                            if (obj != null && obj.optBoolean("no_show", true))
                            {
                                final Service service = new Service();
                                service.setId(obj.optInt("id"));

                                service.setUniq(obj.isNull("machine_name") ? null
                                        : obj.optString("machine_name"));

                                service.setName(obj.isNull("name") ? null : obj.optString("name"));
                                service.setOrder(obj.optInt("order", 0));
                                service.setParentId(obj.optInt("parent", 0));

                                final Service menuService;
                                if ((menuService = menuMap.get(service.getUniq())) != null)
                                {
                                    menuService.setId(service.getId());
                                    continue;
                                }

                                ArrayList<Service> list;
                                if ((list = servicesMap.get(service.getParentId())) != null)
                                    list.add(service);
                                else
                                {
                                    list = new ArrayList<Service>();
                                    list.add(service);
                                    servicesMap.put(service.getParentId(), list);
                                }
                            }
                        }

                        for (final Service service : servicesMenu)
                        {
                            final List<Service> services;
                            if ((services = servicesMap.get(service.getId())) != null)
                            {
                                Collections.sort(services, new Comparator<Service>()
                                {
                                    @Override
                                    public int compare(final Service lhs, final Service rhs)
                                    {
                                        return lhs.getOrder() - rhs.getOrder();
                                    }
                                });
                                service.setServices(services);
                            }
                        }

                        Collections.sort(servicesMenu, new Comparator<Service>()
                        {
                            @Override
                            public int compare(final Service lhs, final Service rhs)
                            {
                                return lhs.getOrder() - rhs.getOrder();
                            }
                        });

                        prefs.put("CACHED_SERVICES", new Gson()
                                .toJsonTree(servicesMenu).getAsJsonArray().toString());
                        cb.onSuccess(servicesMenu);
                    }
                });
            }
        });
    }

    @Override
    public final void authUser(final String email, final String password, final Callback<User> cb)
    {
        service.createUserSession(email, password, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                handleCreateSessionResponse(response, cb);
            }
        });
    }

    public final void getUser(final String userId, final String authToken, final Callback<User> cb)
    {
        service.getUserInfo(userId, authToken, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                handleUserResponse(userId, authToken, response, cb);
            }
        });
    }

    @Override
    public final void getUser(final String email, final Callback<String> cb)
    {
        service.getUserInfo(email, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(response.isNull("name") ? null : response.optString("name"));
            }
        });
    }

    @Override
    public final void updateUser(final User user, final Callback<User> cb)
    {
        service.updateUserInfo(user.getId(), new HandyRetrofitService.UserUpdateRequest(user,
                user.getAuthToken()), new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                handleUserResponse(user.getId(), user.getAuthToken(), response, cb);
            }
        });
    }

    @Override
    public final void getAvailableBookings(final Callback<List<BookingSummary>> cb)
    {
        service.getAvailableBookings(getProviderId(), new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                final List<BookingSummary> bookings = new ArrayList<BookingSummary>();

                for (int i = 0; i < response.length(); i++)
                {
                    try
                    {
                        BookingSummary bs = gsonBuilder.fromJson((response.get(Integer.toString(i)).toString()),
                                new TypeToken<BookingSummary>()
                                {
                                }.getType());
                        bookings.add(bs);
                    } catch (Exception e)
                    {
                        System.err.println("Can not parse BookingSummary " + e);
                    }
                }
                cb.onSuccess(bookings);
            }
        });
    }

    @Override
    public final void getScheduledBookings(final Callback<List<BookingSummary>> cb)
    {
        service.getScheduledBookings(getProviderId(), new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                final List<BookingSummary> bookings = new ArrayList<BookingSummary>();
                for (int i = 0; i < response.length(); i++)
                {
                    try
                    {
                        BookingSummary bs = gsonBuilder.fromJson((response.get(Integer.toString(i)).toString()),
                                new TypeToken<BookingSummary>()
                                {
                                }.getType());
                        bookings.add(bs);
                    } catch (Exception e)
                    {
                        System.err.println("Can not parse BookingSummary " + e);
                    }
                }
                cb.onSuccess(bookings);
            }
        });
    }

    @Override
    public final void claimBooking(String bookingId, final Callback<Booking> cb)
    {
        service.claimBooking(getProviderId(), bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                Booking booking = null;
                try
                {
                    booking = gsonBuilder.fromJson((response.toString()),
                            new TypeToken<Booking>()
                            {
                            }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse Booking" + e);
                }
                cb.onSuccess(booking);
            }
        });
    }

    @Override
    public final void getBookingDetails(String bookingId, final Callback<Booking> cb)
    {
        service.getBookingDetails(getProviderId(), bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                Booking booking = null;
                try
                {
                    booking = gsonBuilder.fromJson((response.toString()),
                            new TypeToken<Booking>()
                            {
                            }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse Booking" + e);
                }
                cb.onSuccess(booking);
            }
        });
    }

    @Override
    public final void requestPinCode(String phoneNumber, final Callback<PinRequestDetails> cb)
    {
        service.requestPinCode(phoneNumber, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                PinRequestDetails pinRequestDetails = null;
                try
                {
                    pinRequestDetails = gsonBuilder.fromJson((response.toString()),
                            new TypeToken<PinRequestDetails>()
                            {
                            }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse PinRequestDetails " + e);
                }
                cb.onSuccess(pinRequestDetails);
            }
        });
    }

    @Override
    public final void checkForUpdates(String appFlavor, int versionCode ,final Callback<UpdateDetails> cb) {

        service.checkUpdates(getProviderId(), appFlavor, versionCode, new HandyRetrofitCallback(cb) {
            @Override
            void success(JSONObject response) {
                UpdateDetails updateDetails = null;
                try {
                    updateDetails = gsonBuilder.fromJson((response.toString()), new TypeToken<UpdateDetails>() {
                    }.getType());
                } catch (Exception e) {
                    System.err.println("Can not parse UpdateDetails " + e);
                }
                cb.onSuccess(updateDetails);
            }
        });

    }



    @Override
    public final void requestLogin(String phoneNumber, String pinCode, final Callback<LoginDetails> cb)
    {
        service.requestLogin(phoneNumber, pinCode, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                LoginDetails loginDetails = null;
                try
                {
                    loginDetails = gsonBuilder.fromJson((response.toString()),
                            new TypeToken<LoginDetails>()
                            {
                            }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse LoginDetails " + e);
                }
                cb.onSuccess(loginDetails);
            }
        });
    }

    private String getProviderId()
    {
        //hack, will eventually point at a real user from our service with an associated ID
        System.err.println("CURRENTLY HACKING USER ID TO : 11");
        return "11";
    }


    private void handleCreateSessionResponse(final JSONObject response, final Callback<User> cb)
    {
        final User user = new User();
        user.setAuthToken(response.isNull("auth_token") ? null : response.optString("auth_token"));
        user.setId(response.isNull("id") ? null : response.optString("id"));
        cb.onSuccess(user);
    }

    private void handleUserResponse(final String userId, final String authToken,
                                    final JSONObject response, final Callback<User> cb)
    {
        final User user = User.fromJson(response.toString());

        user.setAuthToken(authToken);
        user.setId(userId);
        cb.onSuccess(user);
    }

    private String parseAlertMessage(final JSONObject response)
    {
        if (response.optBoolean("alert", false))
        {
            final JSONArray array = response.optJSONArray("messages");
            return array != null && !array.isNull(0) ? array.optString(0) : null;
        } else return null;
    }

}
