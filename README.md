# Java Demo Bank System 

Simple JSP/Servlet based demo bank application
(Home Test Task For A Job)

## Required features:

-**login:**
  * authentication with name and password,
  * error handling

-**logout:**
  * session closing,
  * confirmation for logout

-**menu:**
  * always available in logged state,
  * contains the available functions

-**account balance:**
  * available from menu and after login automatically,
  * contains the logged user's own accounts (account number, currency, current balance)

-**transfer:**
  * available from menu
  * transfer between JUST same currency accounts (source account number, target account number, amount),
  * the logged user's own accounts/source account numbers have to be in a selectable list,
  * source account number can't be same as target account number,
  * the amount can't be float, equal or less than zero, less than balance,
  * the target account number is a custom list, but it can't contain the other users' account numbers,
  * confirmation before the transaction,
  * the transfer process must be atomic,
  * error handling

-**balance history:**
  * available from menu,
  * transfer list of selected account number (source/target account number, currency, amount,transaction type[credit/debit],available balance)