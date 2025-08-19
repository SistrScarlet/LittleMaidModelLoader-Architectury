package net.sistr.littlemaidmodelloader.client.screen.component;

/**
 * フィルタリング条件を定義する関数型インターフェース
 *
 * @param <T> フィルタリング対象の要素型
 */
@FunctionalInterface
public interface FilterPredicate<T> {

    /**
     * 指定された要素と検索文字列に基づいてフィルタリング判定を行う
     *
     * @param element    判定対象の要素
     * @param filterText ユーザーが入力した検索文字列
     * @return 要素が条件に合致する場合はtrue、そうでなければfalse
     */
    boolean test(T element, String filterText);

    /**
     * デフォルトの文字列マッチングフィルター
     * 要素のtoString()結果に対して大文字小文字を無視した部分一致判定を行う
     */
    static <T> FilterPredicate<T> containsIgnoreCase() {
        return (element, filterText) -> {
            if (filterText == null || filterText.trim().isEmpty()) {
                return true; // 空の検索文字列では全て表示
            }
            String elementText = element.toString().toLowerCase();
            String searchText = filterText.toLowerCase().trim();
            return elementText.contains(searchText);
        };
    }

    /**
     * 大文字小文字を区別した部分一致フィルター
     */
    static <T> FilterPredicate<T> contains() {
        return (element, filterText) -> {
            if (filterText == null || filterText.trim().isEmpty()) {
                return true;
            }
            String elementText = element.toString();
            String searchText = filterText.trim();
            return elementText.contains(searchText);
        };
    }

    /**
     * 前方一致フィルター（大文字小文字無視）
     */
    static <T> FilterPredicate<T> startsWithIgnoreCase() {
        return (element, filterText) -> {
            if (filterText == null || filterText.trim().isEmpty()) {
                return true;
            }
            String elementText = element.toString().toLowerCase();
            String searchText = filterText.toLowerCase().trim();
            return elementText.startsWith(searchText);
        };
    }

    /**
     * 正規表現フィルター
     */
    static <T> FilterPredicate<T> regex() {
        return (element, filterText) -> {
            if (filterText == null || filterText.trim().isEmpty()) {
                return true;
            }
            try {
                String elementText = element.toString();
                return elementText.matches(filterText.trim());
            } catch (Exception e) {
                return false; // 正規表現が無効な場合は非表示
            }
        };
    }

    /**
     * ANDロジックで複数のフィルターを組み合わせる
     */
    default FilterPredicate<T> and(FilterPredicate<T> other) {
        return (element, filterText) -> this.test(element, filterText) && other.test(element, filterText);
    }

    /**
     * ORロジックで複数のフィルターを組み合わせる
     */
    default FilterPredicate<T> or(FilterPredicate<T> other) {
        return (element, filterText) -> this.test(element, filterText) || other.test(element, filterText);
    }

    /**
     * 否定フィルター
     */
    default FilterPredicate<T> negate() {
        return (element, filterText) -> !this.test(element, filterText);
    }
}