package com.robinhowlett.chartparser.charts.pdf;

import com.robinhowlett.chartparser.charts.pdf.DistanceSurfaceTrackRecord.TrackRecord;
import com.robinhowlett.chartparser.charts.pdf.running_line.HorseJockey;
import com.robinhowlett.chartparser.charts.pdf.running_line.Odds;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPoolsTest;
import com.robinhowlett.chartparser.fractionals.FractionalPoint;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Fractional;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Split;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition
        .LengthsAhead;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition
        .TotalLengthsBehind;

import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.robinhowlett.chartparser.charts.pdf.Breed.QUARTER_HORSE;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RaceResultBuilderTest {

    @Test
    public void updateStartersWithWinPlaceShowPayoffs_WithCoupledFieldEntries_UpdatesWPSForEach() {
        List<Starter> expected = new ArrayList<Starter>() {{
            Starter first = new Starter.Builder().program("7").horseAndJockey(new HorseJockey(
                    new Horse("Prater Sixty Four"), new Jockey("Karlo", "Lopez"))).build();
            first.setWinPlaceShowPayoff(WagerPayoffPoolsTest.expectedWinPlaceAndShowPayoff());

            Starter second = new Starter.Builder().program("8").horseAndJockey(new HorseJockey(
                    new Horse("Candy Sweetheart"), new Jockey("Dennis", "Collins"))).build();
            second.setWinPlaceShowPayoff(WagerPayoffPoolsTest.expectedPlaceAndShowPayoff());

            Starter third = new Starter.Builder().program("3").horseAndJockey(new HorseJockey(
                    new Horse("Midnightwithdrawal"), new Jockey("Alfredi", "Triana Jr."))).build();
            third.setWinPlaceShowPayoff(WagerPayoffPoolsTest.expectedShowPayoff());

            // null program so payoff should be matched by name
            Starter alsoThird = new Starter.Builder().program(null).horseAndJockey(new HorseJockey(
                    new Horse("Tiznow"), new Jockey("Chris", "McCarron"))).build();
            alsoThird.setWinPlaceShowPayoff(WagerPayoffPoolsTest.expectedShowPayoffNullProgram());

            Starter fifth = new Starter.Builder().program("7A").horseAndJockey(new HorseJockey(
                    new Horse("Rachel Alexandra"), new Jockey("Calvin", "Borel"))).build();
            fifth.setWinPlaceShowPayoff(WagerPayoffPoolsTest.expectedWinPlaceAndShowPayoff());

            Starter sixth = new Starter.Builder().program("4F").horseAndJockey(new HorseJockey(
                    new Horse("Zenyatta"), new Jockey("Mike", "Smith"))).build();
            sixth.setWinPlaceShowPayoff(null);

            Starter seventh = new Starter.Builder().program("7B").horseAndJockey(new HorseJockey(
                    new Horse("Ghostzapper"), new Jockey("Javier", "Castellano"))).build();
            seventh.setWinPlaceShowPayoff(WagerPayoffPoolsTest.expectedWinPlaceAndShowPayoff());

            // null program but should not have any matched payoff
            Starter eighth = new Starter.Builder().program(null).horseAndJockey(new HorseJockey(
                    new Horse("Arazi"), new Jockey("Pat", "Valenzuela"))).build();
            eighth.setWinPlaceShowPayoff(null);

            add(first);
            add(second);
            add(third);
            add(alsoThird);
            add(fifth);
            add(sixth);
            add(seventh);
            add(eighth);
        }};

        RaceResult.Builder raceBuilder = new RaceResult.Builder();
        List<Starter> starters = new ArrayList<Starter>() {{
            add(new Starter.Builder().program("7").horseAndJockey(new HorseJockey(
                    new Horse("Prater Sixty Four"), new Jockey("Karlo", "Lopez"))).build());
            add(new Starter.Builder().program("8").horseAndJockey(new HorseJockey(
                    new Horse("Candy Sweetheart"), new Jockey("Dennis", "Collins"))).build());
            add(new Starter.Builder().program("3").horseAndJockey(new HorseJockey(
                    new Horse("Midnightwithdrawal"), new Jockey("Alfredi", "Triana Jr."))).build());
            add(new Starter.Builder().program(null).horseAndJockey(new HorseJockey(
                    new Horse("Tiznow"), new Jockey("Chris", "McCarron"))).build());
            add(new Starter.Builder().program("7A").horseAndJockey(new HorseJockey(
                    new Horse("Rachel Alexandra"), new Jockey("Calvin", "Borel"))).build());
            add(new Starter.Builder().program("4F").horseAndJockey(new HorseJockey(
                    new Horse("Zenyatta"), new Jockey("Mike", "Smith"))).build());
            add(new Starter.Builder().program("7B").horseAndJockey(new HorseJockey(
                    new Horse("Ghostzapper"), new Jockey("Javier", "Castellano"))).build());
            add(new Starter.Builder().program(null).horseAndJockey(new HorseJockey(
                    new Horse("Arazi"), new Jockey("Pat", "Valenzuela"))).build());
        }};

        WagerPayoffPools expectedWagerPayoffPools = WagerPayoffPoolsTest.expectedWagerPayoffPools();
        // add example to handle when starters have missing program numbers
        expectedWagerPayoffPools.getWinPlaceShowPayoffPools().getWinPlaceShowPayoffs()
                .add(WagerPayoffPoolsTest.expectedShowPayoffNullProgram());

        // method under test
        starters = raceBuilder.updateStartersWithWinPlaceShowPayoffs(starters,
                expectedWagerPayoffPools);

        assertThat(starters, equalTo(expected));
    }

    @Test
    public void updateStartersWithOddsChoiceIndicies_WithFiveStarters_UpdatesChoiceWhenOdds()
            throws Exception {
        List<Starter> expected = new ArrayList<Starter>() {{
            // favorite
            Starter first = new Starter.Builder().odds(new Odds(2.0, true))
                    .horseAndJockey(new HorseJockey(new Horse("Prater Sixty Four"),
                            new Jockey("Karlo", "Lopez"))).build();
            first.setChoice(1);

            // joint-2nd favorite
            Starter second = new Starter.Builder().odds(new Odds(4.0, false))
                    .horseAndJockey(new HorseJockey(new Horse("Candy Sweetheart"),
                            new Jockey("Dennis", "Collins"))).build();
            second.setChoice(2);

            // no odds, therefore no choice
            Starter third = new Starter.Builder()
                    .horseAndJockey(new HorseJockey(new Horse("Midnightwithdrawal"),
                            new Jockey("Alfredi", "Triana Jr."))).build();

            // 4th favorite
            Starter fourth = new Starter.Builder().odds(new Odds(10.0, false))
                    .horseAndJockey(new HorseJockey(new Horse("Prowers County"),
                            new Jockey("Carl", "Williams"))).build();
            fourth.setChoice(4);

            // joint-2nd favorite
            Starter fifth = new Starter.Builder().odds(new Odds(4.0, false))
                    .horseAndJockey(new HorseJockey(new Horse("Al Baz (GB)"),
                            new Jockey("Tracy", "Hebert"))).build();
            fifth.setChoice(2);

            add(first);
            add(second);
            add(third);
            add(fourth);
            add(fifth);
        }};

        RaceResult.Builder raceBuilder = new RaceResult.Builder();
        List<Starter> starters = new ArrayList<Starter>() {{
            add(new Starter.Builder().odds(new Odds(2.0, true))
                    .horseAndJockey(new HorseJockey(new Horse("Prater Sixty Four"),
                            new Jockey("Karlo", "Lopez"))).build());
            add(new Starter.Builder().odds(new Odds(4.0, false))
                    .horseAndJockey(new HorseJockey(new Horse("Candy Sweetheart"),
                            new Jockey("Dennis", "Collins"))).build());
            add(new Starter.Builder()
                    .horseAndJockey(new HorseJockey(new Horse("Midnightwithdrawal"),
                            new Jockey("Alfredi", "Triana Jr."))).build());
            add(new Starter.Builder().odds(new Odds(10.0, false))
                    .horseAndJockey(new HorseJockey(new Horse("Prowers County"),
                            new Jockey("Carl", "Williams"))).build());
            add(new Starter.Builder().odds(new Odds(4.0, false))
                    .horseAndJockey(new HorseJockey(new Horse("Al Baz (GB)"),
                            new Jockey("Tracy", "Hebert"))).build());
        }};

        // method under test
        starters = raceBuilder.updateStartersWithOddsChoiceIndicies(starters);

        assertThat(starters, equalTo(expected));
    }

    @Test
    public void calculateIndividualFractionalsAndSplits_WithTBred_CreatesFractionalsAndSplits()
            throws Exception {
        List<Starter> expectedStarters = new ArrayList<Starter>() {{
            // Back Stop
            Starter.Builder first = new Starter.Builder();
            first.horseAndJockey(new HorseJockey(new Horse("Back Stop"),
                    new Jockey("Dennis", "Collins")));
            first.pointsOfCall(new ArrayList<PointOfCall>() {{
                PointOfCall first = new PointOfCall(1, "Start",
                        "Start", null);
                first.setRelativePosition(new PointOfCall.RelativePosition(1, null));
                add(first);

                PointOfCall second = new PointOfCall(2, "1/4", "2f",
                        1320);
                second.setRelativePosition(
                        new PointOfCall.RelativePosition(1,
                                new PointOfCall.RelativePosition.LengthsAhead("2",
                                        2.0)));
                add(second);

                PointOfCall third = new PointOfCall(3, "1/2", "4f", 2640);
                third.setRelativePosition(
                        new PointOfCall.RelativePosition(1,
                                new PointOfCall.RelativePosition.LengthsAhead("Head",
                                        0.1)));
                add(third);

                PointOfCall fourth = new PointOfCall(5, "Str", "5f",
                        3300);
                fourth.setRelativePosition(
                        new PointOfCall.RelativePosition(1,
                                new PointOfCall.RelativePosition.LengthsAhead("1/2",
                                        0.5)));
                add(fourth);

                PointOfCall fifth = new PointOfCall(6, "Fin", "6f", 3960);
                fifth.setRelativePosition(
                        new PointOfCall.RelativePosition(1,
                                new PointOfCall.RelativePosition.LengthsAhead("1 1/2",
                                        1.5)));
                add(fifth);
            }});
            Fractional frac1_1 = new Fractional(1, "1/4", "2f", 1320, "0:22.880", 22880L);
            Fractional frac1_2 = new Fractional(2, "1/2", "4f", 2640, "0:46.500", 46500L);
            Fractional frac1_3 = new Fractional(3, "5/8", "5f", 3300, "0:59.310", 59310L);
            Fractional frac1_6 = new Fractional(6, "Fin", "6f", 3960, "1:12.980", 72980L);
            Starter firstStarter = first.build();
            firstStarter.setFractionals(new ArrayList<Fractional>() {{
                add(frac1_1);
                add(frac1_2);
                add(frac1_3);
                add(frac1_6);
            }});
            firstStarter.setSplits(new ArrayList<Split>() {{
                add(Split.calculate(null, frac1_1));
                add(Split.calculate(frac1_1, frac1_2));
                add(Split.calculate(frac1_2, frac1_3));
                add(Split.calculate(frac1_3, frac1_6));
            }});
            add(firstStarter);

            // Regal Sunset
            Starter.Builder second = new Starter.Builder();
            second.horseAndJockey(new HorseJockey(new Horse("Regal Sunset"),
                    new Jockey("Vince", "Guerra")));
            second.pointsOfCall(new ArrayList<PointOfCall>() {{
                PointOfCall first = new PointOfCall(1, "Start", "Start", null);
                first.setRelativePosition(new RelativePosition(2, null));
                add(first);

                PointOfCall second = new PointOfCall(2, "1/4", "2f", 1320);
                RelativePosition relativePosition2 = new RelativePosition(2,
                        new LengthsAhead("1 1/2", 1.5));
                relativePosition2.setTotalLengthsBehind(new TotalLengthsBehind
                        ("2", 2.0));
                second.setRelativePosition(relativePosition2);
                add(second);

                PointOfCall third = new PointOfCall(3, "1/2", "4f", 2640);
                RelativePosition relativePosition3 = new RelativePosition(3,
                        new LengthsAhead("4", 4.0));
                relativePosition3.setTotalLengthsBehind(new TotalLengthsBehind
                        ("Head", 0.1));
                third.setRelativePosition(relativePosition3);
                add(third);

                PointOfCall fourth = new PointOfCall(5, "Str", "5f", 3300);
                RelativePosition relativePosition5 = new RelativePosition(2,
                        new LengthsAhead("1", 1.0));
                relativePosition5.setTotalLengthsBehind(new TotalLengthsBehind
                        ("1/2", 0.5));
                fourth.setRelativePosition(relativePosition5);
                add(fourth);

                PointOfCall fifth = new PointOfCall(6, "Fin", "6f", 3960);
                RelativePosition relativePosition6 = new RelativePosition(2,
                        new LengthsAhead("3", 3.0));
                relativePosition6.setTotalLengthsBehind(new TotalLengthsBehind
                        ("1 1/2", 1.5));
                fifth.setRelativePosition(relativePosition6);
                add(fifth);
            }});
            Starter secondStarter = second.build();
            Fractional frac2_1 = new Fractional(1, "1/4", "2f", 1320, "0:23.183", 23183L);
            Fractional frac2_2 = new Fractional(2, "1/2", "4f", 2640, "0:46.515", 46515L);
            Fractional frac2_3 = new Fractional(3, "5/8", "5f", 3300, "0:59.388", 59388L);
            Fractional frac2_6 = new Fractional(6, "Fin", "6f", 3960, "1:13.221", 73221L);
            secondStarter.setFractionals(new ArrayList<Fractional>() {{
                add(frac2_1);
                add(frac2_2);
                add(frac2_3);
                add(frac2_6);
            }});
            secondStarter.setSplits(new ArrayList<Split>() {{
                add(Split.calculate(null, frac2_1));
                add(Split.calculate(frac2_1, frac2_2));
                add(Split.calculate(frac2_2, frac2_3));
                add(Split.calculate(frac2_3, frac2_6));
            }});
            add(secondStarter);
        }};

        // starters for the test
        List<Starter> starters = new ArrayList<Starter>() {{
            // Back Stop
            Starter.Builder first = new Starter.Builder();
            first.horseAndJockey(new HorseJockey(new Horse("Back Stop"),
                    new Jockey("Dennis", "Collins")));
            first.pointsOfCall(new ArrayList<PointOfCall>() {{
                PointOfCall first = new PointOfCall(1, "Start",
                        "Start", null);
                first.setRelativePosition(new PointOfCall.RelativePosition(1, null));
                add(first);

                PointOfCall second = new PointOfCall(2, "1/4", "2f",
                        1320);
                second.setRelativePosition(
                        new PointOfCall.RelativePosition(1,
                                new PointOfCall.RelativePosition.LengthsAhead("2",
                                        2.0)));
                add(second);

                PointOfCall third = new PointOfCall(3, "1/2", "4f", 2640);
                third.setRelativePosition(
                        new PointOfCall.RelativePosition(1,
                                new PointOfCall.RelativePosition.LengthsAhead("Head",
                                        0.1)));
                add(third);

                PointOfCall fourth = new PointOfCall(5, "Str", "5f",
                        3300);
                fourth.setRelativePosition(
                        new PointOfCall.RelativePosition(1,
                                new PointOfCall.RelativePosition.LengthsAhead("1/2",
                                        0.5)));
                add(fourth);

                PointOfCall fifth = new PointOfCall(6, "Fin", "6f", 3960);
                fifth.setRelativePosition(
                        new PointOfCall.RelativePosition(1,
                                new PointOfCall.RelativePosition.LengthsAhead("1 1/2",
                                        1.5)));
                add(fifth);
            }});
            add(first.build());

            // Regal Sunset
            Starter.Builder second = new Starter.Builder();
            second.horseAndJockey(new HorseJockey(new Horse("Regal Sunset"),
                    new Jockey("Vince", "Guerra")));
            second.pointsOfCall(new ArrayList<PointOfCall>() {{
                PointOfCall first = new PointOfCall(1, "Start", "Start", null);
                first.setRelativePosition(new RelativePosition(2, null));
                add(first);

                PointOfCall second = new PointOfCall(2, "1/4", "2f", 1320);
                RelativePosition relativePosition2 = new RelativePosition(2,
                        new LengthsAhead("1 1/2", 1.5));
                relativePosition2.setTotalLengthsBehind(new TotalLengthsBehind
                        ("2", 2.0));
                second.setRelativePosition(relativePosition2);
                add(second);

                PointOfCall third = new PointOfCall(3, "1/2", "4f", 2640);
                RelativePosition relativePosition3 = new RelativePosition(3,
                        new LengthsAhead("4", 4.0));
                relativePosition3.setTotalLengthsBehind(new TotalLengthsBehind
                        ("Head", 0.1));
                third.setRelativePosition(relativePosition3);
                add(third);

                PointOfCall fourth = new PointOfCall(5, "Str", "5f", 3300);
                RelativePosition relativePosition5 = new RelativePosition(2,
                        new LengthsAhead("1", 1.0));
                relativePosition5.setTotalLengthsBehind(new TotalLengthsBehind
                        ("1/2", 0.5));
                fourth.setRelativePosition(relativePosition5);
                add(fourth);

                PointOfCall fifth = new PointOfCall(6, "Fin", "6f", 3960);
                RelativePosition relativePosition6 = new RelativePosition(2,
                        new LengthsAhead("3", 3.0));
                relativePosition6.setTotalLengthsBehind(new TotalLengthsBehind
                        ("1 1/2", 1.5));
                fifth.setRelativePosition(relativePosition6);
                add(fifth);
            }});
            add(second.build());
        }};

        RaceResult.Builder raceBuilder = new RaceResult.Builder();

        List<FractionalPoint.Fractional> fractionals = new ArrayList<>();
        fractionals.add(new FractionalPoint.Fractional(1, "1/4", "2f", 1320, "0:22.880", 22880L));
        fractionals.add(new FractionalPoint.Fractional(2, "1/2", "4f", 2640, "0:46.500", 46500L));
        fractionals.add(new FractionalPoint.Fractional(3, "5/8", "5f", 3300, "0:59.310", 59310L));
        fractionals.add(new FractionalPoint.Fractional(6, "Fin", "6f", 3960, "1:12.980", 72980L));

        // method under test
        starters = raceBuilder.calculateIndividualFractionalsAndSplits(starters, fractionals,
                null, null);

        // check that the each starter's individual fractionals and splits were calculated
        assertThat(starters, equalTo(expectedStarters));
    }

    @Test
    public void calculateIndividualFractionalsAndSplits_WithQH_CreatesFractionalsAndSplits()
            throws Exception {
        List<Starter> expectedStarters = new ArrayList<Starter>() {{
            Starter.Builder first = new Starter.Builder();
            first.horseAndJockey(new HorseJockey(new Horse("Perkin Desire"),
                    new Jockey("Ramiro", "Garcia")));
            first.individualTimeMillis(18015L);
            first.speedIndex(83);
            first.pointsOfCall(new ArrayList<PointOfCall>() {{
                PointOfCall pointOfCall = new PointOfCall(6, "Fin", "350y", 1050);
                pointOfCall.setRelativePosition(new RelativePosition(1,
                        new LengthsAhead("1 3/4", 1.75)));
                add(pointOfCall);
            }});
            Starter firstStarter = first.build();
            final Fractional[] finFirst = {null};
            firstStarter.setFractionals(new ArrayList<Fractional>() {{
                finFirst[0] = new Fractional(6, "Fin", "350y", 1050, "0:18.015", 18015L);
                add(finFirst[0]);
            }});
            firstStarter.setSplits(new ArrayList<Split>() {{
                add(Split.calculate(null, finFirst[0]));
            }});
            add(firstStarter);

            Starter.Builder second = new Starter.Builder();
            second.horseAndJockey(new HorseJockey(new Horse("Ima Cutie Patutie"),
                    new Jockey("Vince", "Guerra")));
            second.individualTimeMillis(18317L);
            second.speedIndex(74);
            second.pointsOfCall(new ArrayList<PointOfCall>() {{
                PointOfCall pointOfCall = new PointOfCall(6, "Fin", "350y", 1050);
                RelativePosition relativePosition = new RelativePosition(2,
                        new LengthsAhead("1/2", 0.5));
                relativePosition.setTotalLengthsBehind(new TotalLengthsBehind("1" +
                        " 3/4", 1.75));
                pointOfCall.setRelativePosition(relativePosition);
                add(pointOfCall);
            }});
            Starter secondStarter = second.build();
            final Fractional[] finSecond = {null};
            secondStarter.setFractionals(new ArrayList<Fractional>() {{
                finSecond[0] = new Fractional(6, "Fin", "350y", 1050, "0:18.317", 18317L);
                add(finSecond[0]);
            }});
            secondStarter.setSplits(new ArrayList<Split>() {{
                add(Split.calculate(null, finSecond[0]));
            }});
            add(secondStarter);
        }};

        List<Starter> starters = new ArrayList<Starter>() {{
            Starter.Builder first = new Starter.Builder();
            first.horseAndJockey(new HorseJockey(new Horse("Perkin Desire"),
                    new Jockey("Ramiro", "Garcia")));
            first.individualTimeMillis(18015L).speedIndex(83)
                    .pointsOfCall(new ArrayList<PointOfCall>() {{
                        PointOfCall pointOfCall = new PointOfCall(6, "Fin", "350y", 1050);
                        pointOfCall.setRelativePosition(new RelativePosition(1,
                                new LengthsAhead("1 3/4", 1.75)));
                        add(pointOfCall);
                    }});
            add(first.build());

            Starter.Builder second = new Starter.Builder();
            second.horseAndJockey(new HorseJockey(new Horse("Ima Cutie Patutie"),
                    new Jockey("Vince", "Guerra")));
            second.individualTimeMillis(18317L).speedIndex(74)
                    .pointsOfCall(new ArrayList<PointOfCall>() {{
                        PointOfCall pointOfCall = new PointOfCall(6, "Fin", "350y", 1050);
                        RelativePosition relativePosition = new RelativePosition(2,
                                new LengthsAhead("1/2", 0.5));
                        relativePosition.setTotalLengthsBehind(new TotalLengthsBehind("1 3/4",
                                1.75));
                        pointOfCall.setRelativePosition(relativePosition);
                        add(pointOfCall);
                    }});
            add(second.build());
        }};

        DistanceSurfaceTrackRecord distanceSurfaceTrackRecord =
                new DistanceSurfaceTrackRecord("Three Hundred And Fifty Yards",
                        "Dirt", null,
                        new TrackRecord(new Horse("Perrys Queen Bug"), "17.045", 17045L,
                                LocalDate.of(2009, 6, 21)));

        RaceTypeNameBlackTypeBreed typeBreed = new RaceTypeNameBlackTypeBreed("", QUARTER_HORSE);

        RaceResult.Builder raceBuilder = new RaceResult.Builder();

        // method under test
        starters = raceBuilder.calculateIndividualFractionalsAndSplits(starters, null, typeBreed,
                distanceSurfaceTrackRecord);

        // check that the each starter's individual fractionals and splits were calculated
        assertThat(starters, equalTo(expectedStarters));

        RaceResult raceResult = raceBuilder.build();

        // check that the race has the winner's fractionals
        Fractional fin = new Fractional(6, "Fin", "350y", 1050, "0:18.015", 18015L);
        List<Fractional> expectedFractionals = new ArrayList<Fractional>() {{
            add(fin);
        }};
        assertThat(raceResult.getFractionals(), equalTo(expectedFractionals));

        // check that the race has the winner's splits
        List<Split> expectedSplits = new ArrayList<Split>() {{
            add(Split.calculate(null, fin));
        }};
        assertThat(raceResult.getSplits(), equalTo(expectedSplits));
    }

    @Test
    public void detectDeadHeat_WithWithUniqueOfficialPositions_ReturnsFalse()
            throws Exception {
        List<Starter> starters = new ArrayList<Starter>() {{
            Starter first = new Starter.Builder().program("7").horseAndJockey(new HorseJockey(
                    new Horse("Prater Sixty Four"), new Jockey("Karlo", "Lopez"))).build();
            first.setOfficialPosition(1);

            Starter second = new Starter.Builder().program("8").horseAndJockey(new HorseJockey(
                    new Horse("Candy Sweetheart"), new Jockey("Dennis", "Collins"))).build();
            second.setOfficialPosition(2);

            Starter third = new Starter.Builder().program("3").horseAndJockey(new HorseJockey(
                    new Horse("Midnightwithdrawal"), new Jockey("Alfredi", "Triana Jr."))).build();
            third.setOfficialPosition(3);

            add(first);
            add(second);
            add(third);
        }};

        RaceResult.Builder raceBuilder = new RaceResult.Builder();
        assertFalse(raceBuilder.detectDeadHeat(starters));
    }

    @Test
    public void detectDeadHeat_WithTwoStartersWithOfficialPositionOne_ReturnsTrue()
            throws Exception {
        List<Starter> starters = new ArrayList<Starter>() {{
            Starter first = new Starter.Builder().program("7").horseAndJockey(new HorseJockey(
                    new Horse("Prater Sixty Four"), new Jockey("Karlo", "Lopez"))).build();
            first.setOfficialPosition(1);

            Starter second = new Starter.Builder().program("8").horseAndJockey(new HorseJockey(
                    new Horse("Candy Sweetheart"), new Jockey("Dennis", "Collins"))).build();
            second.setOfficialPosition(1);

            Starter third = new Starter.Builder().program("3").horseAndJockey(new HorseJockey(
                    new Horse("Midnightwithdrawal"), new Jockey("Alfredi", "Triana Jr."))).build();
            third.setOfficialPosition(3);

            add(first);
            add(second);
            add(third);
        }};

        RaceResult.Builder raceBuilder = new RaceResult.Builder();
        assertTrue(raceBuilder.detectDeadHeat(starters));
    }

    @Test
    public void detectDeadHeat_WithTwoStartersWithOfficialPositionTwo_ReturnsFalse()
            throws Exception {
        List<Starter> starters = new ArrayList<Starter>() {{
            Starter first = new Starter.Builder().program("7").horseAndJockey(new HorseJockey(
                    new Horse("Prater Sixty Four"), new Jockey("Karlo", "Lopez"))).build();
            first.setOfficialPosition(1);

            Starter second = new Starter.Builder().program("8").horseAndJockey(new HorseJockey(
                    new Horse("Candy Sweetheart"), new Jockey("Dennis", "Collins"))).build();
            second.setOfficialPosition(2);

            Starter third = new Starter.Builder().program("3").horseAndJockey(new HorseJockey(
                    new Horse("Midnightwithdrawal"), new Jockey("Alfredi", "Triana Jr."))).build();
            third.setOfficialPosition(2);

            Starter last = new Starter.Builder().program("4").horseAndJockey(new HorseJockey(
                    new Horse("Did Not Finish"), new Jockey("No", "Name"))).build();
            third.setOfficialPosition(null);

            add(first);
            add(second);
            add(third);
            add(last);
        }};

        RaceResult.Builder raceBuilder = new RaceResult.Builder();
        assertFalse(raceBuilder.detectDeadHeat(starters));
    }


    @Test
    public void markCoupledAndFieldEntries_WithTenEntries_MarksNineCoupledOrFieldEntries()
            throws Exception {
        List<Starter> expected = new ArrayList<Starter>() {{
            Starter first = new Starter.Builder().program("2")
                    .horseAndJockey(new HorseJockey(new Horse("Sword Trick"),
                            new Jockey("M.", "Berry"))).build();
            first.setEntry(true);

            Starter second = new Starter.Builder().program("4D")
                    .horseAndJockey(new HorseJockey(new Horse("Boca Bay"),
                            new Jockey("Chris", "Landeros"))).build();
            second.setEntry(true);

            Starter third = new Starter.Builder().program("3c")
                    .horseAndJockey(new HorseJockey(new Horse("Swass Like Me"),
                            new Jockey("Jose", "Medina"))).build();
            third.setEntry(true);

            Starter fourth = new Starter.Builder().program("4F")
                    .horseAndJockey(new HorseJockey(new Horse("Field Goal"),
                            new Jockey("Luis", "Quinonez"))).build();
            fourth.setEntry(true);

            Starter fifth = new Starter.Builder().program("2B")
                    .horseAndJockey(new HorseJockey(new Horse("Coyote Canyon"),
                            new Jockey("Roman", "Chapa"))).build();
            fifth.setEntry(true);

            Starter sixth = new Starter.Builder().program("1a")
                    .horseAndJockey(new HorseJockey(new Horse("Jones Way"),
                            new Jockey("Junior", "Chacaltana"))).build();
            sixth.setEntry(true);

            Starter seventh = new Starter.Builder().program("3F")
                    .horseAndJockey(new HorseJockey(new Horse("Statler"),
                            new Jockey("Erik", "McNeil"))).build();
            seventh.setEntry(true);

            // uncoupled
            Starter eighth = new Starter.Builder().program("5")
                    .horseAndJockey(new HorseJockey(new Horse("Primistalla"),
                            new Jockey("Tony", "McNeil"))).build();
            eighth.setEntry(false);

            Starter ninth = new Starter.Builder().program("1")
                    .horseAndJockey(new HorseJockey(new Horse("Jones Focus"),
                            new Jockey("Gerard", "Melancon"))).build();
            ninth.setEntry(true);

            Starter tenth = new Starter.Builder().program("1X")
                    .horseAndJockey(new HorseJockey(new Horse("Secretariat"),
                            new Jockey("Ron", "Turcotte"))).build();
            tenth.setEntry(true);

            add(first);
            add(second);
            add(third);
            add(fourth);
            add(fifth);
            add(sixth);
            add(seventh);
            add(eighth); // uncoupled
            add(ninth);
            add(tenth);
        }};

        RaceResult.Builder raceBuilder = new RaceResult.Builder();
        List<Starter> starters = new ArrayList<Starter>() {{
            add(new Starter.Builder().program("2")
                    .horseAndJockey(new HorseJockey(new Horse("Sword Trick"),
                            new Jockey("M.", "Berry"))).build());
            add(new Starter.Builder().program("4D")
                    .horseAndJockey(new HorseJockey(new Horse("Boca Bay"),
                            new Jockey("Chris", "Landeros"))).build());
            add(new Starter.Builder().program("3c")
                    .horseAndJockey(new HorseJockey(new Horse("Swass Like Me"),
                            new Jockey("Jose", "Medina"))).build());
            add(new Starter.Builder().program("4F")
                    .horseAndJockey(new HorseJockey(new Horse("Field Goal"),
                            new Jockey("Luis", "Quinonez"))).build());
            add(new Starter.Builder().program("2B")
                    .horseAndJockey(new HorseJockey(new Horse("Coyote Canyon"),
                            new Jockey("Roman", "Chapa"))).build());
            add(new Starter.Builder().program("1a")
                    .horseAndJockey(new HorseJockey(new Horse("Jones Way"),
                            new Jockey("Junior", "Chacaltana"))).build());
            add(new Starter.Builder().program("3F")
                    .horseAndJockey(new HorseJockey(new Horse("Statler"),
                            new Jockey("Erik", "McNeil"))).build());
            add(new Starter.Builder().program("5")
                    .horseAndJockey(new HorseJockey(new Horse("Primistalla"),
                            new Jockey("Tony", "McNeil"))).build());
            add(new Starter.Builder().program("1")
                    .horseAndJockey(new HorseJockey(new Horse("Jones Focus"),
                            new Jockey("Gerard", "Melancon"))).build());
            add(new Starter.Builder().program("1X")
                    .horseAndJockey(new HorseJockey(new Horse("Secretariat"),
                            new Jockey("Ron", "Turcotte"))).build());
        }};

        // method under test
        starters = raceBuilder.markCoupledAndFieldEntries(starters);

        assertThat(starters, equalTo(expected));
    }

    @Test
    public void markPositionDeadHeats_WithDeadHeatForSecond_MarksThosePositionDeadHeatsAsTrue() {
        // expected (has positionDeadHeat == true for second and third)
        List<Starter> expected = new ArrayList<Starter>() {{
            Starter first = new Starter.Builder().program("7").horseAndJockey(new HorseJockey(
                    new Horse("Prater Sixty Four"), new Jockey("Karlo", "Lopez"))).build();
            first.setOfficialPosition(1);
            first.setPositionDeadHeat(false);

            Starter second = new Starter.Builder().program("8").horseAndJockey(new HorseJockey(
                    new Horse("Candy Sweetheart"), new Jockey("Dennis", "Collins"))).build();
            second.setOfficialPosition(2);
            second.setPositionDeadHeat(true);

            Starter third = new Starter.Builder().program("3").horseAndJockey(new HorseJockey(
                    new Horse("Midnightwithdrawal"), new Jockey("Alfredi", "Triana Jr.")))
                    .build();
            third.setOfficialPosition(2);
            third.setPositionDeadHeat(true);

            add(first);
            add(second);
            add(third);
        }};


        // actual
        List<Starter> starters = new ArrayList<Starter>() {{
            Starter first = new Starter.Builder().program("7").horseAndJockey(new HorseJockey(
                    new Horse("Prater Sixty Four"), new Jockey("Karlo", "Lopez"))).build();
            first.setOfficialPosition(1);

            Starter second = new Starter.Builder().program("8").horseAndJockey(new HorseJockey(
                    new Horse("Candy Sweetheart"), new Jockey("Dennis", "Collins"))).build();
            second.setOfficialPosition(2);

            Starter third = new Starter.Builder().program("3").horseAndJockey(new HorseJockey(
                    new Horse("Midnightwithdrawal"), new Jockey("Alfredi", "Triana Jr.")))
                    .build();
            third.setOfficialPosition(2);

            add(first);
            add(second);
            add(third);
        }};

        RaceResult.Builder raceBuilder = new RaceResult.Builder();
        starters = raceBuilder.markPositionDeadHeats(starters);

        // ensure the race isn't reporting a dead heat - that's only for when winners dead heat
        assertFalse(raceBuilder.detectDeadHeat(starters));

        assertThat(starters, equalTo(expected));
    }
}