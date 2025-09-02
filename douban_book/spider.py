
import requests
from bs4 import BeautifulSoup
import time
import pandas as pd
import random
from tqdm import tqdm

# 10 个分类
categories = {
    "小说": "小说",
    "历史": "历史",
    "心理学": "心理学",
    "计算机": "计算机",
    "文学": "文学",
    "传记": "传记",
    "艺术": "艺术",
    "经济学": "经济学",
    "政治": "政治",
    "科学": "科学"
}

# 每个分类抓取数量
BOOKS_PER_CATEGORY = 10
all_books = []
headers = {
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
                  "AppleWebKit/537.36 (KHTML, like Gecko) "
                  "Chrome/139.0.0.0 Safari/537.36",
    "Referer": "https://book.douban.com/",
    "Cookie": "bid=Th-6Po-HVX8; dbcl2=\"203237096:9JOxbsqSmfs\"; ck=aZwU; ll=\"108289\"; _gid=GA1.2.892797493.1756799968; frodotk_db=\"d3adbba1783d11b07b0932e468b73510\"; _pk_ref.100001.3ac3=%5B%22%22%2C%22%22%2C1756799972%2C%22https%3A%2F%2Fm.douban.com%2F%22%5D; _pk_id.100001.3ac3=bcd622234923c0fa.1756799972.; _pk_ses.100001.3ac3=1; __utma=30149280.1100089963.1756799967.1756799972.1756799972.1; __utmc=30149280; __utmz=30149280.1756799972.1.1.utmcsr=m.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utma=81379588.1100089963.1756799967.1756799972.1756799972.1; __utmc=81379588; __utmz=81379588.1756799972.1.1.utmcsr=m.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/; _vwo_uuid_v2=D4FBFD2240CAABFBA769736CAF2366089|94ac6933f60586ef51f720f606617ec1; __yadk_uid=XINf9wz8hrjZlRDIYsYIuferKv9BqtaG; push_noty_num=0; push_doumail_num=0; _ga=GA1.2.1100089963.1756799967; _ga_Y4GN1R87RG=GS2.1.s1756799966$o1$g1$t1756800634$j52$l0$h0; __utmt_douban=1; __utmt=1; __utmb=30149280.13.10.1756799972; __utmb=81379588.13.10.1756799972"
}

seen_books = set()  # 全局去重

def fetch_url(url, retries=3):
    for i in range(retries):
        try:
            resp = requests.get(url, headers=headers, timeout=10)
            if resp.status_code == 200:
                return resp
        except Exception as e:
            print(f"请求异常：{e}, 重试 {i+1}/{retries}")
        time.sleep(2)
    return None

def get_full_description(book_url):
    """抓取书籍详情页完整简介，去除重复段落"""
    resp = fetch_url(book_url)
    if resp is None:
        return "无"
    soup = BeautifulSoup(resp.text, "html.parser")

    # 优先抓折叠后完整内容
    intro = soup.select(".intro.all.hidden")
    if not intro:
        # 没有折叠内容，则抓折叠前简介
        intro = soup.select(".intro")
    if not intro:
        return "无"

    texts = []
    for section in intro:
        for p in section.find_all("p"):
            text = p.get_text(strip=True)
            if text:
                texts.append(text)
        if not section.find_all("p"):
            text = section.get_text(strip=True)
            if text:
                texts.append(text)

    # 去掉重复段落
    seen_paragraphs = set()
    final_texts = []
    for t in texts:
        if t not in seen_paragraphs:
            final_texts.append(t)
            seen_paragraphs.add(t)

    return "\n".join(final_texts)

for cname, ctag in categories.items():
    print(f"\n正在抓取类别：{cname}")
    books = []
    start = 0

    with tqdm(total=BOOKS_PER_CATEGORY, desc=f"{cname}", ncols=100) as pbar:
        while len(books) < BOOKS_PER_CATEGORY:
            url = f"https://book.douban.com/tag/{ctag}?start={start}&type=T"
            resp = fetch_url(url)
            if resp is None:
                break

            soup = BeautifulSoup(resp.text, "html.parser")
            items = soup.select("li.subject-item")
            if not items:
                break  # 翻到最后一页

            for item in items:
                title_tag = item.select_one("h2 a")
                title = title_tag["title"].strip() if title_tag else "未知"
                link = title_tag["href"] if title_tag else None

                pub = item.select_one(".pub")
                author = "未知"
                if pub:
                    parts = pub.text.strip().split("/")
                    if parts:
                        author = parts[0].strip()

                book_id = f"{title}_{author}"
                if book_id in seen_books:
                    continue  # 去重

                seen_books.add(book_id)

                description = get_full_description(link) if link else "无"

                books.append({
                    "分类": cname,
                    "书名": title,
                    "作者": author,
                    "简介": description
                })
                pbar.update(1)

                if len(books) >= BOOKS_PER_CATEGORY:
                    break

            start += 20
            time.sleep(random.uniform(1.5, 3.0))  # 随机休眠

    print(f"✅ {cname} 抓取完成，共 {len(books)} 本（去重后）")
    all_books.extend(books)

# 保存 CSV
df = pd.DataFrame(all_books)
df.to_csv("douban_books.csv", index=False, encoding="utf-8-sig")
print("\n🎉 所有分类爬取完成，结果已保存到 douban_books.csv")

