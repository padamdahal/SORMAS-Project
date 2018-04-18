package de.symeda.sormas.app.core.timeago;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.symeda.sormas.app.R;

import java.util.Date;


public final class TimeAgo implements IWithDate {


    @Nullable
    private Context context;

    /**
     * Instantiates a new Time ago.
     */
    private TimeAgo(Context context) {
        super();
        this.context = context;
    }

    public static IWithDate using(@NonNull Context context) {
        return new TimeAgo(context);
    }

    /**
     * <p>
     * Returns the 'time ago' formatted text using date time.
     * </p>
     *
     * @param time the date time for parsing
     * @return the 'time ago' formatted text using date time
     * @see TimeAgoMessages
     */
    @Override
    public String with(final long time) {
        return with(time, new TimeAgoMessages.Builder(context).build());
    }

    /**
     * <p>
     * Returns the 'time ago' formatted text using date time.
     * </p>
     *
     * @param time      the date time for parsing
     * @param resources the resources for localizing messages
     * @return the 'time ago' formatted text using date time
     * @see TimeAgoMessages
     */
    @Override
    public String with(final long time, final TimeAgoMessages resources) {
        final long dim = getTimeDistanceInMinutes(time);
        final StringBuilder timeAgo = buildTimeagoText(resources, dim);
        return timeAgo.toString();
    }

    @Override
    public String with(Date date) {
        return with(date, new TimeAgoMessages.Builder(context).build());
    }

    @Override
    public String with(Date date, TimeAgoMessages resources) {
        final long dim = getTimeDistanceInMinutes(date.getTime());
        final StringBuilder timeAgo = buildTimeagoText(resources, dim);
        return timeAgo.toString();
    }

    /**
     * Build timeago text string builder.
     *
     * @param resources the resources
     * @param dim       the distance in minutes from now
     * @return the string builder
     */
    private static StringBuilder buildTimeagoText(TimeAgoMessages resources, long dim) {
        final StringBuilder timeAgo = new StringBuilder();

        final Periods foundTimePeriod = Periods.findByDistanceMinutes(dim);
        if (foundTimePeriod != null) {
            final int periodKey = foundTimePeriod.getPropertyKey();
            switch (foundTimePeriod) {
                case XMINUTES_PAST:
                    timeAgo.append(resources.getPropertyValue(periodKey, dim));
                    break;
                case XHOURS_PAST:
                    int hours = Math.round(dim / 60);
                    final String xHoursText = handlePeriodKeyAsPlural(resources,
                            R.string.timeago_aboutanhour_past, periodKey, hours);
                    timeAgo.append(xHoursText);
                    break;
                case XDAYS_PAST:
                    int days = Math.round(dim / 1440);
                    final String xDaysText = handlePeriodKeyAsPlural(resources,
                            R.string.timeago_oneday_past, periodKey, days);
                    timeAgo.append(xDaysText);
                    break;
                case XWEEKS_PAST:
                    int weeks = Math.round(dim / 7560);
                    final String xWeeksText = handlePeriodKeyAsPlural(resources,
                            R.string.timeago_oneweek_past, periodKey, weeks);
                    timeAgo.append(xWeeksText);
                    break;
                case XMONTHS_PAST:
                    int months = Math.round(dim / 43200);
                    final String xMonthsText = handlePeriodKeyAsPlural(resources,
                            R.string.timeago_aboutamonth_past, periodKey, months);
                    timeAgo.append(xMonthsText);
                    break;
                case XYEARS_PAST:
                    int years = Math.round(dim / 525600);
                    timeAgo.append(resources.getPropertyValue(periodKey, years));
                    break;
                case XMINUTES_FUTURE:
                    timeAgo.append(resources.getPropertyValue(periodKey, Math.abs((float) dim)));
                    break;
                case XHOURS_FUTURE:
                    int hours1 = Math.abs(Math.round(dim / 60f));
                    final String yHoursText = hours1 == 24
                            ? resources.getPropertyValue(R.string.timeago_oneday_future)
                            : handlePeriodKeyAsPlural(resources, R.string.timeago_aboutanhour_future,
                            periodKey, hours1);
                    timeAgo.append(yHoursText);
                    break;
                case XDAYS_FUTURE:
                    int days1 = Math.abs(Math.round(dim / 1440f));
                    final String yDaysText = handlePeriodKeyAsPlural(resources,
                            R.string.timeago_oneday_future, periodKey, days1);
                    timeAgo.append(yDaysText);
                    break;
                case XWEEKS_FUTURE:
                    int weeks1 = Math.abs(Math.round(dim / 7560f));
                    final String yWeeksText = handlePeriodKeyAsPlural(resources,
                            R.string.timeago_oneweek_future, periodKey, weeks1);
                    timeAgo.append(yWeeksText);
                case XMONTHS_FUTURE:
                    int months1 = Math.abs(Math.round(dim / 43200f));
                    final String yMonthsText = months1 == 12
                            ? resources.getPropertyValue(R.string.timeago_aboutayear_future)
                            : handlePeriodKeyAsPlural(resources,
                            R.string.timeago_aboutamonth_future, periodKey, months1);
                    timeAgo.append(yMonthsText);
                    break;
                case XYEARS_FUTURE:
                    int years1 = Math.abs(Math.round(dim / 525600f));
                    timeAgo.append(resources.getPropertyValue(periodKey, years1));
                    break;
                default:
                    timeAgo.append(resources.getPropertyValue(periodKey));
                    break;
            }
        }
        return timeAgo;
    }

    /**
     * Handle period key as plural string.
     *
     * @param resources the resources
     * @param periodKey the period key
     * @param value     the value
     * @return the string
     */
    private static String handlePeriodKeyAsPlural(final TimeAgoMessages resources,
                                                  final int periodKey,
                                                  final int pluralKey, final int value) {
        return value == 1
                ? resources.getPropertyValue(periodKey)
                : resources.getPropertyValue(pluralKey, value);
    }

    /**
     * Returns the time distance in minutes.
     *
     * @param time the date time
     * @return the time distance in minutes
     */
    private static long getTimeDistanceInMinutes(long time) {
        long timeDistance = System.currentTimeMillis() - time;
        return Math.round((timeDistance / 1000) / 60);
    }

    /**
     * The enum Periods.
     *
     * @author marlonlom
     * @version 3.0.1
     * @since 2.0.0
     */
    private enum Periods {

        NOW(R.string.timeago_now, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance == 0;
            }
        }),
        ONEMINUTE_PAST(R.string.timeago_oneminute_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance == 1;
            }
        }),
        XMINUTES_PAST(R.string.timeago_xminutes_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 2 && distance <= 44;
            }
        }),
        ABOUTANHOUR_PAST(R.string.timeago_aboutanhour_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 45 && distance <= 89;
            }
        }),
        XHOURS_PAST(R.string.timeago_xhours_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 90 && distance <= 1439;
            }
        }),
        ONEDAY_PAST(R.string.timeago_oneday_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 1440 && distance <= 2519;
            }
        }),
        XDAYS_PAST(R.string.timeago_xdays_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 2520 && distance <= 7559;
            }
        }),
        ONEWEEK_PAST(R.string.timeago_oneweek_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 7560 && distance <= 14918;
            }
        }),
        XWEEKS_PAST(R.string.timeago_xweeks_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 14919 && distance <= 43199;
            }
        }),
        ABOUTAMONTH_PAST(R.string.timeago_aboutamonth_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 43200 && distance <= 86399;
            }
        }),
        XMONTHS_PAST(R.string.timeago_xmonths_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 86400 && distance <= 525599;
            }
        }),
        ABOUTAYEAR_PAST(R.string.timeago_aboutayear_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 525600 && distance <= 655199;
            }
        }),
        OVERAYEAR_PAST(R.string.timeago_overayear_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 655200 && distance <= 914399;
            }
        }),
        ALMOSTTWOYEARS_PAST(R.string.timeago_almosttwoyears_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= 914400 && distance <= 1051199;
            }
        }),
        XYEARS_PAST(R.string.timeago_xyears_past, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return Math.round(distance / 525600) > 1;
            }
        }),
        ONEMINUTE_FUTURE(R.string.timeago_oneminute_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance == -1;
            }
        }),
        XMINUTES_FUTURE(R.string.timeago_xminutes_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance <= -2 && distance >= -44;
            }
        }),
        ABOUTANHOUR_FUTURE(R.string.timeago_aboutanhour_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance <= -45 && distance >= -89;
            }
        }),
        XHOURS_FUTURE(R.string.timeago_xhours_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance <= -90 && distance >= -1439;
            }
        }),
        ONEDAY_FUTURE(R.string.timeago_oneday_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance <= -1440 && distance >= -2519;
            }
        }),
        XDAYS_FUTURE(R.string.timeago_xdays_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance <= -2520 && distance >= -7559;
            }
        }),
        ONEWEEK_FUTURE(R.string.timeago_oneweek_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= -7560 && distance <= -14918;
            }
        }),
        XWEEKS_FUTURE(R.string.timeago_xweeks_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance >= -14919 && distance <= -43199;
            }
        }),
        ABOUTAMONTH_FUTURE(R.string.timeago_aboutamonth_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance <= -43200 && distance >= -86399;
            }
        }),
        XMONTHS_FUTURE(R.string.timeago_xmonths_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance <= -86400 && distance >= -525599;
            }
        }),
        ABOUTAYEAR_FUTURE(R.string.timeago_aboutayear_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance <= -525600 && distance >= -655199;
            }
        }),
        OVERAYEAR_FUTURE(R.string.timeago_overayear_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance <= -655200 && distance >= -914399;
            }
        }),
        ALMOSTTWOYEARS_FUTURE(R.string.timeago_almosttwoyears_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return distance <= -914400 && distance >= -1051199;
            }
        }),
        XYEARS_FUTURE(R.string.timeago_xyears_future, new DistancePredicate() {
            @Override
            public boolean validateDistanceMinutes(final long distance) {
                return Math.round(distance / 525600) < -1;
            }
        });

        /**
         * The property key.
         */
        private int mPropertyKey;
        /**
         * The predicate.
         */
        private DistancePredicate mPredicate;

        Periods(int propertyKey, DistancePredicate predicate) {
            this.mPropertyKey = propertyKey;
            this.mPredicate = predicate;
        }

        /**
         * Find by distance minutes periods.
         *
         * @param distanceMinutes the distance minutes
         * @return the periods
         */
        public static Periods findByDistanceMinutes(final long distanceMinutes) {
            final Periods[] values = Periods.values();
            for (final Periods item : values) {
                final boolean successful = item.getPredicate()
                        .validateDistanceMinutes(distanceMinutes);
                if (successful) {
                    return item;
                }
            }
            return null;
        }

        /**
         * Gets predicate.
         *
         * @return the predicate
         */
        private DistancePredicate getPredicate() {
            return mPredicate;
        }

        /**
         * Gets property key.
         *
         * @return the property key
         */
        public int getPropertyKey() {
            return mPropertyKey;
        }
    }

    /**
     * Interface definition for handling distance validations or periods.
     *
     * @author marlonlom
     * @version 3.0.1
     * @see Periods
     * @since 1.0.0
     */
    private interface DistancePredicate {
        /**
         * Validate distance minutes boolean.
         *
         * @param distance the distance
         * @return the boolean
         */
        boolean validateDistanceMinutes(final long distance);
    }
}