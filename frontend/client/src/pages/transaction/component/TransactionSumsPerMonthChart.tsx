import { useContext, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { newLogger } from "../../../core/utils";
import { AuthenticationContext } from "../../../security/authentication/authentication-provider";
import { Account, CurrencyType } from "../../account/account";
import { TransactionSumsPerMonth, TransactionType } from "../transaction";
import { getTransactionValuesPerMonthApi } from "../transaction-api";
import * as Highcharts from 'highcharts';
import HighchartsReact from 'highcharts-react-official';

const log = newLogger('pages/account/component/TransactionSumsPerMonthChart');
const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
const initialOptions: Highcharts.Options = {
    chart: {
        type: 'column'
    },
    tooltip: {
        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
        pointFormat: `
            <tr><td style="color:{series.color};padding:0">{series.name}: </td>
            <td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>
        `,
        footerFormat: '</table>',
        shared: true,
        useHTML: true
    },
    plotOptions: {
        column: {
            pointPadding: 0.1,
            borderWidth: 0
        }
    }
}

export interface TransactionSumsPerMonthChartProps {
    account: Account,
    username: string
}

const TransactionSumsPerMonthChart: React.FC<TransactionSumsPerMonthChartProps> = ({ account: { id, currency, money }, username }) => {

    // Contexts
    const { axiosInstance } = useContext(AuthenticationContext);

    // State
    const [options, setOptions] = useState<Highcharts.Options>({
        ...initialOptions,
        title: {
            text: `<b>${username} ${money} ${currency}</b>`,
            useHTML: true
        },
        yAxis: {
            min: 0,
            title: {
                text: currency.toString()
            }
        }
    });

    // Effects
    useEffect(() => {
        let cancelled = false;
        getTransactionValuesPerMonthApi(axiosInstance!, id!)
            .then((result) => {
                log('{__get}', 'success');
                if (cancelled) {
                    return;
                }

                const months = result.map(t => monthNames[t.month - 1]);
                const income = result.map(t => t.income);
                const expense = result.map(t => t.expense);
                setOptions({
                    ...options,
                    xAxis: {
                        categories: months,
                        crosshair: true
                    },
                    series: [
                        {
                            name: 'Income',
                            data: income,
                            type: "bar"
                        },
                        {
                            name: 'Expense',
                            data: expense,
                            type: "bar"
                        }
                    ]
                })
            })
            .catch((error) => {
                log('{__get}', 'failure');
                toast.error(error);
            });


        return () => {
            cancelled = true;
        }
    }, []);

    return (
        <>
            <HighchartsReact
                highcharts={Highcharts}
                options={options}
            />
        </>
    )
}

export default TransactionSumsPerMonthChart;
